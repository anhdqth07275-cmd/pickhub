package service;

import dto.KetQuaPhieuDTO;
import entity.PhieuGiamGia;
import exception.NghiepVuException;
import repository.PhieuGiamGiaRepository;
import repository.SuDungPhieuGiamGiaRepository;
import util.DinhDangUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Nghiep vu phieu giam gia (module M4).
 * - Quan ly phieu: them / sua / tam dung / chay lai
 * - kiemTraVaTinhGiam(): HAM DUNG CHUNG cho M5 (ap phieu o gio hang) va M6 (kiem tra lai luc dat hang)
 */
public class PhieuGiamGiaService {

    private final PhieuGiamGiaRepository phieuRepo = new PhieuGiamGiaRepository();
    private final SuDungPhieuGiamGiaRepository suDungRepo = new SuDungPhieuGiamGiaRepository();

    // =====================================================================
    // 1. QUAN LY PHIEU
    // =====================================================================

    // note code nhu nay nhe ban: chua den gio = SAP_CHAY, qua gio = HET_HAN, con lai = DANG_CHAY
    public static String trangThaiTheoThoiGian(LocalDateTime batDau, LocalDateTime ketThuc, LocalDateTime bayGio) {
        if (bayGio.isBefore(batDau)) return "SAP_CHAY";
        if (bayGio.isAfter(ketThuc)) return "HET_HAN";
        return "DANG_CHAY";
    }

    /** Them moi (id = null) hoac sua phieu giam gia. */
    public void luuPhieu(PhieuGiamGia p) {
        // ---- ma phieu: bat buoc, <= 30 ky tu, IN HOA, khong khoang trang, khong trung ----
        String ma = p.getMaGiamGia() == null ? "" : p.getMaGiamGia().trim().toUpperCase();
        if (ma.isEmpty()) throw new NghiepVuException("Mã giảm giá không được để trống");
        if (ma.length() > 30) throw new NghiepVuException("Mã giảm giá tối đa 30 ký tự");
        if (ma.contains(" ")) throw new NghiepVuException("Mã giảm giá không được chứa khoảng trắng");
        p.setMaGiamGia(ma);
        if (phieuRepo.daTonTai("maGiamGia", ma, p.getMaPhieuGiamGia())) {
            throw new NghiepVuException("Mã giảm giá " + ma + " đã tồn tại");
        }

        String ten = p.getTenPhieu() == null ? "" : p.getTenPhieu().trim();
        if (ten.isEmpty()) throw new NghiepVuException("Tên phiếu không được để trống");
        if (ten.length() > 150) throw new NghiepVuException("Tên phiếu tối đa 150 ký tự");
        p.setTenPhieu(ten);

        // ---- loai giam + gia tri ----
        if (!"PHAN_TRAM".equals(p.getLoaiGiam()) && !"SO_TIEN".equals(p.getLoaiGiam())) {
            throw new NghiepVuException("Loại giảm không hợp lệ");
        }
        if (p.getGiaTriGiam() == null || p.getGiaTriGiam().signum() <= 0) {
            throw new NghiepVuException("Giá trị giảm phải lớn hơn 0");
        }
        // note code nhu nay nhe ban: khop CHECK CK_PGG_giaTri (PHAN_TRAM <= 100)
        if ("PHAN_TRAM".equals(p.getLoaiGiam()) && p.getGiaTriGiam().compareTo(new BigDecimal("100")) > 0) {
            throw new NghiepVuException("Phần trăm giảm tối đa là 100");
        }
        p.setGiaTriGiam(p.getGiaTriGiam().setScale(2, RoundingMode.HALF_UP));

        // note code nhu nay nhe ban: giamToiDa CHI dung cho PHAN_TRAM; phieu SO_TIEN thi xoa di cho sach du lieu
        if ("SO_TIEN".equals(p.getLoaiGiam())) {
            p.setGiamToiDa(null);
        } else if (p.getGiamToiDa() != null && p.getGiamToiDa().signum() <= 0) {
            throw new NghiepVuException("Mức giảm tối đa phải lớn hơn 0 (để trống = không giới hạn)");
        }

        // ---- dieu kien ap dung ----
        if (p.getGiaTriDonToiThieu() == null) p.setGiaTriDonToiThieu(BigDecimal.ZERO);
        if (p.getGiaTriDonToiThieu().signum() < 0) throw new NghiepVuException("Giá trị đơn tối thiểu không được âm");

        if (p.getGioiHanMoiNguoi() == null || p.getGioiHanMoiNguoi() < 1) {
            throw new NghiepVuException("Giới hạn mỗi người phải từ 1 trở lên");
        }
        int daDung = p.getSoLanDaSuDung() == null ? 0 : p.getSoLanDaSuDung();
        if (p.getGioiHanSuDung() != null) {
            if (p.getGioiHanSuDung() < 1) throw new NghiepVuException("Giới hạn tổng lượt dùng phải từ 1 trở lên (để trống = không giới hạn)");
            // note code nhu nay nhe ban: khop CHECK CK_PGG_luotDung (soLanDaSuDung <= gioiHanSuDung)
            if (p.getGioiHanSuDung() < daDung) {
                throw new NghiepVuException("Giới hạn tổng lượt dùng không được nhỏ hơn số lượt đã dùng (" + daDung + ")");
            }
        }

        // ---- thoi gian ----
        if (p.getNgayBatDau() == null || p.getNgayKetThuc() == null) throw new NghiepVuException("Phải chọn thời gian bắt đầu và kết thúc");
        if (!p.getNgayKetThuc().isAfter(p.getNgayBatDau())) throw new NghiepVuException("Ngày kết thúc phải sau ngày bắt đầu");

        // ---- luu ----
        LocalDateTime bayGio = LocalDateTime.now();
        if (p.getMaPhieuGiamGia() == null) {
            p.setSoLanDaSuDung(0);
            p.setTrangThai(trangThaiTheoThoiGian(p.getNgayBatDau(), p.getNgayKetThuc(), bayGio));
            phieuRepo.luu(p);
        } else {
            // dang TAM_DUNG thi giu nguyen, con lai tinh lai theo thoi gian moi
            if (!"TAM_DUNG".equals(p.getTrangThai())) {
                p.setTrangThai(trangThaiTheoThoiGian(p.getNgayBatDau(), p.getNgayKetThuc(), bayGio));
            }
            phieuRepo.capNhat(p);
        }
    }

    /** Tam dung <-> chay lai. Phieu HET_HAN thi khong doi duoc. */
    public void doiTrangThai(Long maPhieu) {
        PhieuGiamGia p = phieuRepo.timTheoId(maPhieu);
        if (p == null) throw new NghiepVuException("Không tìm thấy phiếu giảm giá");

        if ("TAM_DUNG".equals(p.getTrangThai())) {
            // note code nhu nay nhe ban: chay lai thi tinh trang thai theo thoi gian hien tai
            p.setTrangThai(trangThaiTheoThoiGian(p.getNgayBatDau(), p.getNgayKetThuc(), LocalDateTime.now()));
        } else if ("SAP_CHAY".equals(p.getTrangThai()) || "DANG_CHAY".equals(p.getTrangThai())) {
            p.setTrangThai("TAM_DUNG");
        } else {
            throw new NghiepVuException("Phiếu đã hết hạn, không thể đổi trạng thái");
        }
        phieuRepo.capNhat(p);
    }

    /** Scheduler goi moi phut. */
    public int capNhatTrangThaiTheoThoiGian() {
        return phieuRepo.capNhatTrangThaiTheoThoiGian(LocalDateTime.now());
    }

    // =====================================================================
    // 2. KIEM TRA VA TINH TIEN GIAM  (M5 / M6 goi)
    // =====================================================================

    /**
     * Kiem tra phieu co dung duoc cho don co tong tam tinh = tamTinh khong, neu duoc thi tinh so tien giam.
     * KHONG nem loi khi phieu khong hop le: tra ve KetQuaPhieuDTO.tuChoi(lyDo) de giao dien hien thi.
     * maKhachHang = null thi bo qua buoc kiem tra gioi han moi nguoi (dung khi admin thu phieu).
     * Doi chieu du lieu mau: don 4.926.600 + DOUBLE11 (11%) = 541.926.
     */
    public KetQuaPhieuDTO kiemTraVaTinhGiam(String maGiamGia, Long maKhachHang, BigDecimal tamTinh) {
        if (maGiamGia == null || maGiamGia.trim().isEmpty()) return KetQuaPhieuDTO.tuChoi("Vui lòng nhập mã giảm giá");
        if (tamTinh == null) tamTinh = BigDecimal.ZERO;

        PhieuGiamGia p = phieuRepo.timTheoMa(maGiamGia);
        if (p == null) return KetQuaPhieuDTO.tuChoi("Mã giảm giá không tồn tại");

        // note code nhu nay nhe ban: kiem tra theo thu tu tu "phieu co dung duoc khong" den "don co du dieu kien khong"
        // 1. trang thai phieu
        if ("TAM_DUNG".equals(p.getTrangThai())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá đang tạm dừng");
        if ("HET_HAN".equals(p.getTrangThai())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá đã hết hạn");
        if ("SAP_CHAY".equals(p.getTrangThai())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá chưa đến thời gian áp dụng");

        // 2. kiem tra lai thoi gian (scheduler chi chay moi phut nen co the lech toi da 1 phut)
        LocalDateTime bayGio = LocalDateTime.now();
        if (bayGio.isBefore(p.getNgayBatDau())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá chưa đến thời gian áp dụng");
        if (bayGio.isAfter(p.getNgayKetThuc())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá đã hết hạn");

        // 3. con luot tong (gioiHanSuDung = null la khong gioi han)
        if (p.getGioiHanSuDung() != null && p.getSoLanDaSuDung() >= p.getGioiHanSuDung()) {
            return KetQuaPhieuDTO.tuChoi("Mã giảm giá đã hết lượt sử dụng");
        }

        // 4. gioi han tren moi khach (chi dem luot DA_SU_DUNG)
        if (maKhachHang != null && suDungRepo.demLuotDung(p.getMaPhieuGiamGia(), maKhachHang) >= p.getGioiHanMoiNguoi()) {
            return KetQuaPhieuDTO.tuChoi("Bạn đã dùng mã này tối đa " + p.getGioiHanMoiNguoi() + " lần");
        }

        // 5. don toi thieu
        if (tamTinh.compareTo(p.getGiaTriDonToiThieu()) < 0) {
            return KetQuaPhieuDTO.tuChoi("Đơn hàng tối thiểu " + DinhDangUtil.tien(p.getGiaTriDonToiThieu()) + " mới được áp dụng mã này");
        }

        return KetQuaPhieuDTO.thanhCong(p, tinhSoTienGiam(p, tamTinh));
    }

    // tinh so tien giam cua 1 phieu tren tamTinh
    private BigDecimal tinhSoTienGiam(PhieuGiamGia p, BigDecimal tamTinh) {
        BigDecimal giam;
        if ("PHAN_TRAM".equals(p.getLoaiGiam())) {
            // note code nhu nay nhe ban: tamTinh x % / 100, lam tron den dong, roi bi chan boi giamToiDa (neu co)
            giam = tamTinh.multiply(p.getGiaTriGiam()).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
            if (p.getGiamToiDa() != null && giam.compareTo(p.getGiamToiDa()) > 0) giam = p.getGiamToiDa();
        } else {
            giam = p.getGiaTriGiam().setScale(0, RoundingMode.HALF_UP);
        }
        // note code nhu nay nhe ban: khop CHECK CK_HoaDon_tien (soTienGiam <= tamTinh) nen tien giam khong bao gio vuot tien hang
        if (giam.compareTo(tamTinh) > 0) giam = tamTinh;
        return giam;
    }
}
