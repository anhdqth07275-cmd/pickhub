package service;

import dto.BoLoc;
import dto.DongKhuyenMaiDTO;
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
import util.GiaoDich;
import util.NhapLieuUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Nghiep vu chuong trinh khuyen mai (module M4).
 * - Quan ly chuong trinh: them / sua / tam dung / chay lai / xoa
 * - Popup "Them / Sua chuong trinh": luu chuong trinh KEM danh sach san pham tich chon (luuChuongTrinhKemSanPham)
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

    // =====================================================================
    // 5. POPUP "THEM / SUA CHUONG TRINH" : TICH CHON SAN PHAM + MUC GIAM, LUU 1 LAN
    // =====================================================================

    /**
     * Luu chuong trinh + TOAN BO san pham duoc tich chon trong 1 lan bam "Luu chuong trinh".
     *
     * @param ct          chuong trinh (moi hoac dang sua), da set ten / mo ta / ngay bat dau / ngay ket thuc
     * @param dsDong      cac dong DA TICH CHON (loaiGiam, giaTriGiam, soLuongGioiHan la chuoi nguoi dung nhap)
     * @param suaSanPham  false = chi sua thong tin chuong trinh, KHONG dung toi danh sach san pham
     *                    (vd chuong trinh da ket thuc: popup an bang san pham)
     *
     * note code nhu nay nhe ban: thu tu lam viec:
     *   1) kiem tra HET moi dong (loi o bat ky dong nao thi nem loi ngay, CHUA ghi gi vao DB)
     *   2) luu chuong trinh (luuChuongTrinh co san: validate ten, thoi gian...)
     *   3) ghi cac dong san pham trong 1 transaction: them dong moi, sua dong da co, go dong bi bo tich
     */
    public void luuChuongTrinhKemSanPham(ChuongTrinhKhuyenMai ct, Long maNguoiTao, List<DongKhuyenMaiDTO> dsDong, boolean suaSanPham) {
        if (!suaSanPham) {
            luuChuongTrinh(ct, maNguoiTao);
            return;
        }
        boolean themMoi = ct.getMaChuongTrinhKhuyenMai() == null;
        if (dsDong == null || dsDong.isEmpty()) {
            throw new NghiepVuException("Vui lòng tích chọn ít nhất 1 sản phẩm áp dụng khuyến mãi");
        }
        if (!themMoi) kiemTraChuongTrinhConSua(ct.getMaChuongTrinhKhuyenMai());

        // cac dong DA CO trong chuong trinh (key = ma bien the) de biet dong nao la moi / sua / bi go
        Map<Long, ChiTietChuongTrinhKhuyenMai> dongCu = new HashMap<>();
        if (!themMoi) {
            for (ChiTietChuongTrinhKhuyenMai d : chiTietRepo.timTheoChuongTrinh(ct.getMaChuongTrinhKhuyenMai())) {
                dongCu.put(d.getChiTietSanPham().getMaChiTietSanPham(), d);
            }
        }

        // ---- 1. kiem tra tung dong da tich ----
        List<ChiTietChuongTrinhKhuyenMai> canThem = new ArrayList<>();
        List<ChiTietChuongTrinhKhuyenMai> canSua = new ArrayList<>();
        Set<Long> giuLai = new HashSet<>();
        for (DongKhuyenMaiDTO dong : dsDong) {
            // note code nhu nay nhe ban: tai lai bien the tu DB (khong tin du lieu tu trinh duyet) de lay gia goc / trang thai moi nhat
            ChiTietSanPham sp = sanPhamRepo.timTheoId(dong.getBienThe().getMaChiTietSanPham());
            if (sp == null) throw new NghiepVuException("Có sản phẩm không còn tồn tại, vui lòng tải lại trang");
            if (!giuLai.add(sp.getMaChiTietSanPham())) continue;           // tich trung 1 san pham 2 lan -> bo qua lan sau

            ChiTietChuongTrinhKhuyenMai d = dongCu.get(sp.getMaChiTietSanPham());
            String nhan = "[" + sp.getMaHang() + "] ";                       // gan ma hang vao thong bao loi de biet dong nao sai
            if (d == null && !"DANG_BAN".equals(sp.getTrangThai())) {
                throw new NghiepVuException(nhan + "sản phẩm không ở trạng thái đang bán nên không thể thêm vào chương trình");
            }
            BigDecimal giaTri;
            Integer soLuong;
            try {
                giaTri = NhapLieuUtil.docSo(dong.getGiaTriGiam(), "Giá trị giảm");
                soLuong = NhapLieuUtil.docSoNguyen(dong.getSoLuongGioiHan(), "Giới hạn số lượng");
                // note code nhu nay nhe ban: dung lai kiemTraMucGiam co san (PHAN_TRAM trong (0,100], SO_TIEN < gia goc, soLuongDaBan <= gioi han)
                kiemTraMucGiam(dong.getLoaiGiam(), giaTri, sp.getGiaGoc(), soLuong, d == null || d.getSoLuongDaBan() == null ? 0 : d.getSoLuongDaBan());
            } catch (NghiepVuException e) {
                throw new NghiepVuException(nhan + e.getMessage());
            }

            if (d == null) {
                d = new ChiTietChuongTrinhKhuyenMai();
                d.setChiTietSanPham(sp);
                d.setSoLuongDaBan(0);
                canThem.add(d);
            } else {
                canSua.add(d);
            }
            d.setLoaiGiam(dong.getLoaiGiam());
            d.setGiaTriGiam(giaTri.setScale(2, RoundingMode.HALF_UP));
            d.setSoLuongGioiHan(soLuong);
        }

        // dong da co nhung bi BO TICH -> go ra khoi chuong trinh (neu da co hoa don dung thi khong go duoc, giong goChiTiet)
        List<Long> canGo = new ArrayList<>();
        for (Map.Entry<Long, ChiTietChuongTrinhKhuyenMai> e : dongCu.entrySet()) {
            if (giuLai.contains(e.getKey())) continue;
            ChiTietChuongTrinhKhuyenMai d = e.getValue();
            if (chiTietRepo.demHoaDonDung(d.getMaChiTietChuongTrinhKhuyenMai()) > 0) {
                throw new NghiepVuException("[" + d.getChiTietSanPham().getMaHang() + "] đã có hóa đơn dùng mức giảm này nên không thể bỏ chọn. "
                        + "Hãy giữ tích chọn, hoặc tạm dừng chương trình nếu muốn ngừng giảm giá");
            }
            canGo.add(d.getMaChiTietChuongTrinhKhuyenMai());
        }

        // ---- 2. luu chuong trinh (ham co san, tu validate ten / thoi gian) ----
        luuChuongTrinh(ct, maNguoiTao);

        // ---- 3. ghi cac dong san pham trong 1 transaction ----
        final Long maChuongTrinh = ct.getMaChuongTrinhKhuyenMai();
        GiaoDich.chay(s -> {
            for (ChiTietChuongTrinhKhuyenMai d : canThem) {
                d.setMaChuongTrinhKhuyenMai(maChuongTrinh);
                s.persist(d);
            }
            for (ChiTietChuongTrinhKhuyenMai d : canSua) s.merge(d);
            for (Long id : canGo) {
                ChiTietChuongTrinhKhuyenMai d = s.get(ChiTietChuongTrinhKhuyenMai.class, id);
                if (d != null) s.remove(d);
            }
            return null;
        });
    }

    /** XOA chuong trinh. Da co hoa don dung bat ky dong nao cua chuong trinh thi khong xoa duoc (se loi khoa ngoai) -> bao loi, goi y Tam dung. */
    public void xoaChuongTrinh(Long maChuongTrinh) {
        ChuongTrinhKhuyenMai ct = ctRepo.timTheoId(maChuongTrinh);
        if (ct == null) throw new NghiepVuException("Không tìm thấy chương trình khuyến mãi");
        for (ChiTietChuongTrinhKhuyenMai d : chiTietRepo.timTheoChuongTrinh(maChuongTrinh)) {
            if (chiTietRepo.demHoaDonDung(d.getMaChiTietChuongTrinhKhuyenMai()) > 0) {
                throw new NghiepVuException("Chương trình \"" + ct.getTenChuongTrinh() + "\" đã có hóa đơn áp dụng nên không thể xóa. "
                        + "Hãy dùng \"Tạm dừng\" để ngừng giảm giá");
            }
        }
        ctRepo.xoa(maChuongTrinh);
    }

    /** Cac san pham DA co trong chuong trinh, dang o dang "da tich chon" de popup Sua hien san. */
    public List<DongKhuyenMaiDTO> layDongDaChon(Long maChuongTrinh) {
        List<DongKhuyenMaiDTO> ds = new ArrayList<>();
        for (ChiTietChuongTrinhKhuyenMai d : chiTietRepo.timTheoChuongTrinh(maChuongTrinh)) {
            DongKhuyenMaiDTO dong = new DongKhuyenMaiDTO(d.getChiTietSanPham());
            dong.setChon(true);
            dong.setMaDong(d.getMaChiTietChuongTrinhKhuyenMai());
            dong.setLoaiGiam(d.getLoaiGiam());
            dong.setGiaTriGiam(d.getGiaTriGiam().stripTrailingZeros().toPlainString());   // 15.00 -> "15", 300000.00 -> "300000"
            dong.setSoLuongGioiHan(d.getSoLuongGioiHan() == null ? "" : String.valueOf(d.getSoLuongGioiHan()));
            dong.setSoLuongDaBan(d.getSoLuongDaBan() == null ? 0 : d.getSoLuongDaBan());
            ds.add(dong);
        }
        return ds;
    }

    /**
     * Tim san pham (bien the DANG_BAN) de tich chon: theo ma hang hoac ten san pham. Khong go gi = lay san pham dau tien theo ma hang.
     * soLuong gioi han so dong tra ve de popup luon nhe du du lieu rat lon; muon thay them thi go tu khoa de thu hep.
     * boQua = cac bien the da co san trong popup (khong tra lai).
     */
    public List<DongKhuyenMaiDTO> timSanPhamDeChon(String tuKhoa, int soLuong, Set<Long> boQua) {
        BoLoc loc = new BoLoc().cotTim("maHang,sanPham.tenSanPham").tuKhoa(tuKhoa)
                .cotTrangThai("trangThai").trangThai("DANG_BAN")
                .sapXep("x.maHang asc").trang(1).kichThuoc(soLuong + (boQua == null ? 0 : boQua.size()));
        List<DongKhuyenMaiDTO> ds = new ArrayList<>();
        for (ChiTietSanPham sp : sanPhamRepo.timKiem(loc).getDanhSach()) {
            if (boQua != null && boQua.contains(sp.getMaChiTietSanPham())) continue;
            if (ds.size() >= soLuong) break;
            ds.add(new DongKhuyenMaiDTO(sp));
        }
        return ds;
    }
}
