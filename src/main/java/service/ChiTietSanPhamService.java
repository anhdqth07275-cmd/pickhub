package service;

import dto.ThongTinBanDTO;
import entity.*;
import exception.NghiepVuException;
import repository.*;
import util.DocSoUtil;
import util.GiaoDich;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Quan ly BIEN THE san pham (M3): 1 san pham co nhieu ChiTietSanPham (mau x khoi luong x chat lieu x chu vi tay cam).
 * Cung la noi cap du lieu cho M4 / M5: layThongTinBan, kiemTraCoTheMua.
 */
public class ChiTietSanPhamService {

    private final ChiTietSanPhamRepository repo = new ChiTietSanPhamRepository();
    private final SanPhamRepository sanPhamRepo = new SanPhamRepository();
    private final MauSacRepository mauSacRepo = new MauSacRepository();
    private final KhoiLuongRepository khoiLuongRepo = new KhoiLuongRepository();
    private final ChatLieuRepository chatLieuRepo = new ChatLieuRepository();
    private final ChuViTayCamRepository chuViRepo = new ChuViTayCamRepository();
    private final HinhAnhSanPhamService hinhAnhService = new HinhAnhSanPhamService();

    public List<ChiTietSanPham> layTheoSanPham(Long maSanPham) {
        return repo.layTheoSanPham(maSanPham);
    }

    public ChiTietSanPham layTheoId(Long id) {
        return repo.timTheoId(id);
    }

    // ---------------- THEM / SUA ----------------
    /**
     * Them moi (id = null) hoac sua (id != null). Sai nghiep vu thi nem NghiepVuException kem thong bao tieng Viet.
     * Cac gia tri so (doDayMm, giaGoc, soLuongTon, nguong) nhan dang chuoi de bao loi dung ten truong khi nhap sai.
     */
    public ChiTietSanPham luu(Long id, Long maSanPham, String maHang, Long maMau, Long maKhoiLuong, Long maChatLieu,
                              Long maChuVi, String doDayMm, String giaGoc, String soLuongTon, String nguong) {
        // ---- 1. san pham cha ----
        SanPham sp = sanPhamRepo.timTheoId(maSanPham);
        if (sp == null) throw new NghiepVuException("Không tìm thấy sản phẩm");

        // ---- 2. ma hang (SKU): chu in hoa, so, gach ngang / gach duoi, khong trung ----
        maHang = maHang == null ? "" : maHang.trim().toUpperCase();
        if (maHang.isEmpty()) throw new NghiepVuException("Mã hàng (SKU) không được để trống");
        if (maHang.length() > 50) throw new NghiepVuException("Mã hàng tối đa 50 ký tự");
        if (!maHang.matches("[A-Z0-9_\\-]+")) throw new NghiepVuException("Mã hàng chỉ gồm chữ không dấu, số, dấu - và _");
        if (repo.trungMaHang(maHang, id)) throw new NghiepVuException("Mã hàng \"" + maHang + "\" đã tồn tại");

        // ---- 3. thuoc tinh (neu chon) phai ton tai va dang HOAT_DONG ----
        MauSac mau = maMau == null ? null : mauSacRepo.timTheoId(maMau);
        KhoiLuong kl = maKhoiLuong == null ? null : khoiLuongRepo.timTheoId(maKhoiLuong);
        ChatLieu cl = maChatLieu == null ? null : chatLieuRepo.timTheoId(maChatLieu);
        ChuViTayCam cv = maChuVi == null ? null : chuViRepo.timTheoId(maChuVi);
        if (maMau != null && (mau == null || !"HOAT_DONG".equals(mau.getTrangThai()))) throw new NghiepVuException("Màu sắc không hợp lệ hoặc đã ngừng");
        if (maKhoiLuong != null && (kl == null || !"HOAT_DONG".equals(kl.getTrangThai()))) throw new NghiepVuException("Khối lượng không hợp lệ hoặc đã ngừng");
        if (maChatLieu != null && (cl == null || !"HOAT_DONG".equals(cl.getTrangThai()))) throw new NghiepVuException("Chất liệu không hợp lệ hoặc đã ngừng");
        if (maChuVi != null && (cv == null || !"HOAT_DONG".equals(cv.getTrangThai()))) throw new NghiepVuException("Chu vi tay cầm không hợp lệ hoặc đã ngừng");

        // note code nhu nay nhe ban: khong cho 2 bien the cua cung san pham co cung to hop (Den + 225g + Carbon + 4.25)
        if (repo.trungToHop(maSanPham, maMau, maKhoiLuong, maChatLieu, maChuVi, id)) {
            throw new NghiepVuException("Sản phẩm này đã có biến thể cùng màu / khối lượng / chất liệu / chu vi tay cầm");
        }

        // ---- 4. cac gia tri so ----
        BigDecimal gia = DocSoUtil.soThapPhan(giaGoc, "Giá gốc");
        if (gia == null) throw new NghiepVuException("Giá gốc không được để trống");
        gia = gia.setScale(0, RoundingMode.HALF_UP);           // cot DECIMAL(12,0): khong co phan le
        if (gia.signum() <= 0) throw new NghiepVuException("Giá gốc phải lớn hơn 0");
        if (gia.compareTo(new BigDecimal("999999999999")) > 0) throw new NghiepVuException("Giá gốc quá lớn");

        Integer ton = DocSoUtil.soNguyen(soLuongTon, "Số lượng tồn");
        if (ton == null) ton = 0;
        if (ton < 0) throw new NghiepVuException("Số lượng tồn không được âm");   // luat quan trong cua ton kho

        Integer nguongCanhBao = DocSoUtil.soNguyen(nguong, "Ngưỡng cảnh báo tồn kho");
        if (nguongCanhBao == null) nguongCanhBao = 5;
        if (nguongCanhBao < 0) throw new NghiepVuException("Ngưỡng cảnh báo không được âm");

        BigDecimal doDay = DocSoUtil.soThapPhan(doDayMm, "Độ dày");
        if (doDay != null && (doDay.signum() <= 0 || doDay.compareTo(new BigDecimal("99.9")) > 0)) {
            throw new NghiepVuException("Độ dày phải lớn hơn 0 và không quá 99.9 mm");
        }

        // ---- 5. luu ----
        ChiTietSanPham ct = new ChiTietSanPham();
        if (id != null) {
            ct = repo.timTheoId(id);
            if (ct == null) throw new NghiepVuException("Không tìm thấy biến thể");
            // note code nhu nay nhe ban: bien the khong duoc chuyen sang san pham khac
            if (!ct.getSanPham().getMaSanPham().equals(maSanPham)) throw new NghiepVuException("Biến thể không thuộc sản phẩm này");
        } else {
            ct.setSanPham(sp);
            ct.setTrangThai("DANG_BAN");
        }
        ct.setMaHang(maHang);
        ct.setMauSac(mau);
        ct.setKhoiLuong(kl);
        ct.setChatLieu(cl);
        ct.setChuViTayCam(cv);
        ct.setDoDayMm(doDay);
        ct.setGiaGoc(gia);
        ct.setSoLuongTon(ton);
        ct.setNguongCanhBaoTonKho(nguongCanhBao);
        TonKhoService.dongBoTrangThai(ct);      // ton = 0 -> HET_HANG, co hang -> DANG_BAN

        if (id == null) repo.luu(ct); else repo.capNhat(ct);
        return ct;
    }

    // ---------------- NGUNG BAN / BAN LAI / XOA ----------------
    /** Doi qua lai NGUNG_BAN <-> ban lai (ban lai thi trang thai tu tinh theo ton kho: DANG_BAN hoac HET_HANG). */
    public void doiTrangThaiBan(Long id) {
        ChiTietSanPham ct = repo.timTheoId(id);
        if (ct == null) throw new NghiepVuException("Không tìm thấy biến thể");
        if ("NGUNG_BAN".equals(ct.getTrangThai())) {
            ct.setTrangThai("DANG_BAN");
            TonKhoService.dongBoTrangThai(ct);
        } else {
            ct.setTrangThai("NGUNG_BAN");
        }
        repo.capNhat(ct);
    }

    // note code nhu nay nhe ban: bien the da nam trong hoa don / khuyen mai thi KHONG xoa cung (loi khoa ngoai va mat lich su), chi cho NGUNG_BAN
    public void xoa(final Long id) {
        if (repo.timTheoId(id) == null) throw new NghiepVuException("Không tìm thấy biến thể");
        if (repo.demDaDuocSuDung(id) > 0) {
            throw new NghiepVuException("Biến thể đã có trong đơn hàng hoặc khuyến mãi, hãy chuyển sang Ngừng bán thay vì xóa");
        }
        GiaoDich.chay(s -> {
            s.createQuery("delete from HinhAnhSanPham h where h.maChiTietSanPham = :id").setParameter("id", id).executeUpdate();
            s.createQuery("delete from ChiTietSanPham c where c.maChiTietSanPham = :id").setParameter("id", id).executeUpdate();
            return null;
        });
    }

    // ---------------- API CHO M4 / M5 ----------------
    /** Thong tin ban cua 1 bien the (gia goc, ton, trang thai, anh...). Khong tim thay -> NghiepVuException. */
    public ThongTinBanDTO layThongTinBan(Long maChiTietSanPham) {
        ChiTietSanPham ct = repo.timTheoId(maChiTietSanPham);
        if (ct == null) throw new NghiepVuException("Sản phẩm không tồn tại");
        return taoThongTinBan(ct);
    }

    /** Nhieu bien the cung luc (vd hien thi gio hang). Bien the khong ton tai thi bo qua. */
    public List<ThongTinBanDTO> layThongTinBan(List<Long> danhSachMa) {
        List<ThongTinBanDTO> kq = new ArrayList<ThongTinBanDTO>();
        for (Long ma : danhSachMa) {
            ChiTietSanPham ct = repo.timTheoId(ma);
            if (ct != null) kq.add(taoThongTinBan(ct));
        }
        return kq;
    }

    /**
     * M5 goi khi THEM GIO HANG va luc DAT HANG: nem loi neu khong the mua (an / ngung ban / het hang / vuot ton).
     * Tra ve thong tin ban de M5 dung tiep, khong phai goi them lan nua.
     */
    public ThongTinBanDTO kiemTraCoTheMua(Long maChiTietSanPham, int soLuong) {
        if (soLuong <= 0) throw new NghiepVuException("Số lượng phải lớn hơn 0");
        ThongTinBanDTO t = layThongTinBan(maChiTietSanPham);
        if ("NGUNG_BAN".equals(t.getTrangThai())) throw new NghiepVuException("Sản phẩm " + t.getTenSanPham() + " đã ngừng bán");
        if (!t.isCoTheMua()) throw new NghiepVuException("Sản phẩm " + t.getTenSanPham() + " hiện không còn bán");
        if (t.getSoLuongTon() < soLuong) throw new NghiepVuException("Sản phẩm " + t.getTenSanPham() + " chỉ còn " + t.getSoLuongTon() + " cái");
        return t;
    }

    private ThongTinBanDTO taoThongTinBan(ChiTietSanPham ct) {
        SanPham sp = ct.getSanPham();
        ThongTinBanDTO t = new ThongTinBanDTO();
        t.setMaChiTietSanPham(ct.getMaChiTietSanPham());
        t.setMaSanPham(sp.getMaSanPham());
        t.setTenSanPham(sp.getTenSanPham());
        t.setMaHang(ct.getMaHang());
        t.setMoTaChiTiet(taoMoTa(ct));
        t.setGiaGoc(ct.getGiaGoc());
        t.setSoLuongTon(ct.getSoLuongTon());
        t.setTrangThai(ct.getTrangThai());
        t.setAnhChinh(layAnhChoBienThe(sp.getMaSanPham(), ct.getMaChiTietSanPham()));
        // note code nhu nay nhe ban: chi MUA DUOC khi san pham dang hien thi + bien the dang ban + con hang
        t.setCoTheMua(Boolean.TRUE.equals(sp.getDangHienThi()) && "DANG_BAN".equals(ct.getTrangThai()) && ct.getSoLuongTon() > 0);
        return t;
    }

    /** Anh rieng cua bien the neu co, khong thi anh chinh cua san pham, khong thi anh mac dinh. */
    private String layAnhChoBienThe(Long maSanPham, Long maChiTietSanPham) {
        for (HinhAnhSanPham a : hinhAnhService.layTheoSanPham(maSanPham)) {
            if (maChiTietSanPham.equals(a.getMaChiTietSanPham())) return a.getDuongDanAnh();
        }
        return hinhAnhService.layAnhChinh(maSanPham);
    }

    /** Mo ta ngan gon cua bien the, vd "Đen / 225g / Carbon / 4.25 inch". M5 luu chuoi nay vao HoaDonChiTiet.moTaChiTiet. */
    public static String taoMoTa(ChiTietSanPham ct) {
        List<String> phan = new ArrayList<String>();
        if (ct.getMauSac() != null) phan.add(ct.getMauSac().getTenMauSac());
        if (ct.getKhoiLuong() != null) phan.add(ct.getKhoiLuong().getKhoiLuongGram() + "g");
        if (ct.getChatLieu() != null) phan.add(ct.getChatLieu().getTenChatLieu());
        if (ct.getChuViTayCam() != null) phan.add(ct.getChuViTayCam().getChuViInch().stripTrailingZeros().toPlainString() + " inch");
        return String.join(" / ", phan);
    }
}
