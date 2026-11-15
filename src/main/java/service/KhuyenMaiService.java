package service;

import dto.KetQuaGiaDTO;
import entity.ChiTietChuongTrinhKhuyenMai;
import entity.ChiTietSanPham;
import entity.ChuongTrinhKhuyenMai;
import exception.NghiepVuException;
import org.hibernate.Session;
import repository.ChiTietChuongTrinhKhuyenMaiRepository;
import repository.ChiTietSanPhamRepository;
import repository.ChuongTrinhKhuyenMaiRepository;
import util.DinhDangUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Nghiep vu chuong trinh khuyen mai (module M4).
 * - Quan ly chuong trinh: them / sua / tam dung / chay lai
 * - Quan ly muc giam cua tung chi tiet san pham trong chuong trinh
 * - tinhGiaSauKhuyenMai(): HAM DUNG CHUNG cho M3 (hien gia), M5 (gio hang), M6 (dat hang)
 */
public class KhuyenMaiService {

    private final ChuongTrinhKhuyenMaiRepository ctRepo = new ChuongTrinhKhuyenMaiRepository();
    private final ChiTietChuongTrinhKhuyenMaiRepository chiTietRepo = new ChiTietChuongTrinhKhuyenMaiRepository();
    private final ChiTietSanPhamRepository sanPhamRepo = new ChiTietSanPhamRepository();

    // =====================================================================
    // 1. CHUONG TRINH KHUYEN MAI
    // =====================================================================

    // note code nhu nay nhe ban: trang thai suy ra tu thoi gian: chua den gio = SAP_CHAY, qua gio = KET_THUC, con lai = DANG_CHAY
    public static String trangThaiTheoThoiGian(LocalDateTime batDau, LocalDateTime ketThuc, LocalDateTime bayGio) {
        if (bayGio.isBefore(batDau)) return "SAP_CHAY";
        if (bayGio.isAfter(ketThuc)) return "KET_THUC";
        return "DANG_CHAY";
    }

    /** Them moi (id = null) hoac sua chuong trinh. maNguoiTao = nhan vien dang dang nhap (chi dung khi them moi). */
    public void luuChuongTrinh(ChuongTrinhKhuyenMai ct, Long maNguoiTao) {
        // ---- validate theo rang buoc SQL ----
        String ten = ct.getTenChuongTrinh() == null ? "" : ct.getTenChuongTrinh().trim();
        if (ten.isEmpty()) throw new NghiepVuException("Tên chương trình không được để trống");
        if (ten.length() > 150) throw new NghiepVuException("Tên chương trình tối đa 150 ký tự");
        ct.setTenChuongTrinh(ten);
        if (ct.getMoTa() != null && ct.getMoTa().length() > 500) throw new NghiepVuException("Mô tả tối đa 500 ký tự");

        if (ct.getNgayBatDau() == null || ct.getNgayKetThuc() == null) {
            throw new NghiepVuException("Phải chọn thời gian bắt đầu và kết thúc");
        }
        // note code nhu nay nhe ban: khop CHECK CK_CTKM_thoiGian (ngayKetThuc > ngayBatDau)
        if (!ct.getNgayKetThuc().isAfter(ct.getNgayBatDau())) {
            throw new NghiepVuException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        LocalDateTime bayGio = LocalDateTime.now();
        if (ct.getMaChuongTrinhKhuyenMai() == null) {
            // them moi: luu nguoi tao + trang thai theo thoi gian
            ct.setMaNguoiTao(maNguoiTao);
            ct.setTrangThai(trangThaiTheoThoiGian(ct.getNgayBatDau(), ct.getNgayKetThuc(), bayGio));
            ctRepo.luu(ct);
        } else {
            // note code nhu nay nhe ban: dang TAM_DUNG thi giu nguyen (admin chu dong dung), con lai tinh lai theo thoi gian moi
            if (!"TAM_DUNG".equals(ct.getTrangThai())) {
                ct.setTrangThai(trangThaiTheoThoiGian(ct.getNgayBatDau(), ct.getNgayKetThuc(), bayGio));
            }
            ctRepo.capNhat(ct);
        }
    }

    /** Tam dung <-> chay lai. Chuong trinh da KET_THUC thi khong doi duoc. */
    public void doiTrangThai(Long maChuongTrinh) {
        ChuongTrinhKhuyenMai ct = ctRepo.timTheoId(maChuongTrinh);
        if (ct == null) throw new NghiepVuException("Không tìm thấy chương trình khuyến mãi");

        if ("TAM_DUNG".equals(ct.getTrangThai())) {
            // note code nhu nay nhe ban: chay lai thi tinh trang thai theo thoi gian hien tai (co the thanh SAP_CHAY / DANG_CHAY / KET_THUC)
            ct.setTrangThai(trangThaiTheoThoiGian(ct.getNgayBatDau(), ct.getNgayKetThuc(), LocalDateTime.now()));
        } else if ("SAP_CHAY".equals(ct.getTrangThai()) || "DANG_CHAY".equals(ct.getTrangThai())) {
            ct.setTrangThai("TAM_DUNG");
        } else {
            throw new NghiepVuException("Chương trình đã kết thúc, không thể đổi trạng thái");
        }
        ctRepo.capNhat(ct);
    }

    /** Scheduler goi moi phut. Tra ve so chuong trinh da doi trang thai. */
    public int capNhatTrangThaiTheoThoiGian() {
        return ctRepo.capNhatTrangThaiTheoThoiGian(LocalDateTime.now());
    }

    // =====================================================================
    // 2. MUC GIAM CUA TUNG CHI TIET SAN PHAM TRONG CHUONG TRINH
    // =====================================================================

    public void themChiTiet(Long maChuongTrinh, Long maChiTietSanPham, String loaiGiam, BigDecimal giaTriGiam, Integer soLuongGioiHan) {
        kiemTraChuongTrinhConSua(maChuongTrinh);

        ChiTietSanPham sp = sanPhamRepo.timTheoId(maChiTietSanPham);
        if (sp == null) throw new NghiepVuException("Vui lòng chọn sản phẩm (biến thể) hợp lệ");

        // note code nhu nay nhe ban: UNIQUE (chuong trinh, bien the) - 1 bien the chi xuat hien 1 lan trong 1 chuong trinh
        if (chiTietRepo.daTonTaiBienThe(maChuongTrinh, maChiTietSanPham, null)) {
            throw new NghiepVuException("Sản phẩm " + sp.getMaHang() + " đã có trong chương trình này");
        }
        kiemTraMucGiam(loaiGiam, giaTriGiam, sp.getGiaGoc(), soLuongGioiHan, 0);

        ChiTietChuongTrinhKhuyenMai d = new ChiTietChuongTrinhKhuyenMai();
        d.setMaChuongTrinhKhuyenMai(maChuongTrinh);
        d.setChiTietSanPham(sp);
        d.setLoaiGiam(loaiGiam);
        d.setGiaTriGiam(giaTriGiam.setScale(2, RoundingMode.HALF_UP));
        d.setSoLuongGioiHan(soLuongGioiHan);
        d.setSoLuongDaBan(0);
        chiTietRepo.luu(d);
    }

    public void suaChiTiet(Long maChuongTrinh, Long maChiTiet, String loaiGiam, BigDecimal giaTriGiam, Integer soLuongGioiHan) {
        kiemTraChuongTrinhConSua(maChuongTrinh);
        ChiTietChuongTrinhKhuyenMai d = timDongCuaChuongTrinh(maChuongTrinh, maChiTiet);

        // note code nhu nay nhe ban: sua muc giam chi anh huong don dat SAU nay, hoa don cu da luu gia chup lai (donGia) nen khong doi
        kiemTraMucGiam(loaiGiam, giaTriGiam, d.getChiTietSanPham().getGiaGoc(), soLuongGioiHan, d.getSoLuongDaBan());
        d.setLoaiGiam(loaiGiam);
        d.setGiaTriGiam(giaTriGiam.setScale(2, RoundingMode.HALF_UP));
        d.setSoLuongGioiHan(soLuongGioiHan);
        chiTietRepo.capNhat(d);
    }

    public void goChiTiet(Long maChuongTrinh, Long maChiTiet) {
        kiemTraChuongTrinhConSua(maChuongTrinh);
        timDongCuaChuongTrinh(maChuongTrinh, maChiTiet);

        // note code nhu nay nhe ban: HoaDonChiTiet co khoa ngoai toi dong nay -> da co hoa don dung thi khong xoa duoc (se loi FK), chi tam dung chuong trinh
        if (chiTietRepo.demHoaDonDung(maChiTiet) > 0) {
            throw new NghiepVuException("Đã có hóa đơn dùng mức giảm này nên không thể gỡ. Hãy tạm dừng chương trình nếu muốn ngừng giảm giá");
        }
        chiTietRepo.xoa(maChiTiet);
    }

    // chuong trinh phai ton tai va chua KET_THUC thi moi duoc them / sua / go san pham
    private void kiemTraChuongTrinhConSua(Long maChuongTrinh) {
        ChuongTrinhKhuyenMai ct = ctRepo.timTheoId(maChuongTrinh);
        if (ct == null) throw new NghiepVuException("Không tìm thấy chương trình khuyến mãi");
        if ("KET_THUC".equals(ct.getTrangThai())) throw new NghiepVuException("Chương trình đã kết thúc, không thể chỉnh sửa sản phẩm");
    }

    // lay dong chi tiet va chac chan no thuoc dung chuong trinh (tranh sua nham dong cua chuong trinh khac)
    private ChiTietChuongTrinhKhuyenMai timDongCuaChuongTrinh(Long maChuongTrinh, Long maChiTiet) {
        ChiTietChuongTrinhKhuyenMai d = chiTietRepo.timTheoId(maChiTiet);
        if (d == null || !d.getMaChuongTrinhKhuyenMai().equals(maChuongTrinh)) {
            throw new NghiepVuException("Không tìm thấy dòng sản phẩm trong chương trình");
        }
        return d;
    }

    // note code nhu nay nhe ban: PHAN_TRAM trong (0, 100]; SO_TIEN > 0 va nho hon gia goc (gia sau giam khong am); soLuongDaBan <= soLuongGioiHan
    private void kiemTraMucGiam(String loaiGiam, BigDecimal giaTri, BigDecimal giaGoc, Integer soLuongGioiHan, int soLuongDaBan) {
        if (!"PHAN_TRAM".equals(loaiGiam) && !"SO_TIEN".equals(loaiGiam)) {
            throw new NghiepVuException("Loại giảm không hợp lệ");
        }
        if (giaTri == null || giaTri.signum() <= 0) {
            throw new NghiepVuException("Giá trị giảm phải lớn hơn 0");
        }
        if ("PHAN_TRAM".equals(loaiGiam) && giaTri.compareTo(new BigDecimal("100")) > 0) {
            throw new NghiepVuException("Phần trăm giảm phải trong khoảng (0, 100]");
        }
        if ("SO_TIEN".equals(loaiGiam) && giaTri.compareTo(giaGoc) >= 0) {
            throw new NghiepVuException("Số tiền giảm phải nhỏ hơn giá gốc (" + DinhDangUtil.tien(giaGoc) + ")");
        }
        if (soLuongGioiHan != null) {
            if (soLuongGioiHan < 1) throw new NghiepVuException("Giới hạn số lượng phải từ 1 trở lên (để trống = không giới hạn)");
            if (soLuongGioiHan < soLuongDaBan) {
                throw new NghiepVuException("Giới hạn số lượng không được nhỏ hơn số đã bán (" + soLuongDaBan + ")");
            }
        }
    }

    // =====================================================================
    // 3. TINH GIA SAU KHUYEN MAI  (M3 / M5 / M6 goi)
    // =====================================================================

    public KetQuaGiaDTO tinhGiaSauKhuyenMai(Long maChiTietSanPham) {
        ChiTietSanPham sp = sanPhamRepo.timTheoId(maChiTietSanPham);
        if (sp == null) throw new NghiepVuException("Không tìm thấy chi tiết sản phẩm");
        return tinhGiaSauKhuyenMai(sp);
    }

    /**
     * Gia ban hien tai cua 1 bien the:
     * chi tinh chuong trinh DANG_CHAY, dang trong thoi gian, con luot; neu co nhieu chuong trinh thi lay GIA THAP NHAT (khong cong don).
     */
    public KetQuaGiaDTO tinhGiaSauKhuyenMai(ChiTietSanPham sp) {
        BigDecimal giaGoc = sp.getGiaGoc();
        KetQuaGiaDTO tot = new KetQuaGiaDTO(giaGoc, giaGoc, null);   // mac dinh: khong khuyen mai

        for (ChiTietChuongTrinhKhuyenMai d : chiTietRepo.timKhuyenMaiDangChayCuaBienThe(sp.getMaChiTietSanPham(), LocalDateTime.now())) {
            BigDecimal gia = tinhGia(giaGoc, d.getLoaiGiam(), d.getGiaTriGiam());
            if (gia.compareTo(tot.getGiaSauGiam()) < 0) {
                tot = new KetQuaGiaDTO(giaGoc, gia, d.getMaChiTietChuongTrinhKhuyenMai());
            }
        }
        return tot;
    }

    /**
     * Cong thuc gia sau giam cho 1 muc giam.
     * Doi chieu du lieu mau: gia goc 4.890.000 giam 15% = 4.156.500.
     */
    public static BigDecimal tinhGia(BigDecimal giaGoc, String loaiGiam, BigDecimal giaTriGiam) {
        BigDecimal gia;
        if ("PHAN_TRAM".equals(loaiGiam)) {
            // note code nhu nay nhe ban: gia goc x (100 - %) / 100, lam tron den dong (HALF_UP)
            gia = giaGoc.multiply(new BigDecimal("100").subtract(giaTriGiam))
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
        } else {
            gia = giaGoc.subtract(giaTriGiam);
        }
        // note code nhu nay nhe ban: khop CHECK CK_HDCT_gia (0 <= donGia <= donGiaGoc)
        if (gia.signum() < 0) gia = BigDecimal.ZERO;
        if (gia.compareTo(giaGoc) > 0) gia = giaGoc;
        return gia.setScale(0, RoundingMode.HALF_UP);
    }

    // =====================================================================
    // 4. GHI NHAN / HOAN SO LUONG DA BAN  (M6 goi trong transaction dat hang / huy don)
    // =====================================================================

    /** Dat hang: tang soLuongDaBan cua muc giam. maChiTietCTKM = null (dong khong co khuyen mai) thi bo qua. */
    public void ghiNhanDaBan(Session session, Long maChiTietCTKM, int soLuong) {
        if (maChiTietCTKM == null) return;
        if (!chiTietRepo.tangSoLuongDaBan(session, maChiTietCTKM, soLuong)) {
            // throw -> M6 rollback ca don hang
            throw new NghiepVuException("Khuyến mãi đã hết số lượng, vui lòng tải lại giỏ hàng");
        }
    }

    /** Huy don: hoan lai luot khuyen mai. */
    public void hoanDaBan(Session session, Long maChiTietCTKM, int soLuong) {
        if (maChiTietCTKM == null) return;
        chiTietRepo.hoanSoLuongDaBan(session, maChiTietCTKM, soLuong);
    }
}
