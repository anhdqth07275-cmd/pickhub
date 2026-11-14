package service;

import dto.DongGioHangDTO;
import dto.GioHangDTO;
import dto.KetQuaGiaDTO;
import dto.KetQuaPhieuDTO;
import entity.*;
import exception.NghiepVuException;
import org.hibernate.Session;
import util.DinhDangUtil;
import util.GiaoDich;
import util.HibernateConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nghiep vu GIO HANG (module M5).
 * Gio hang = 1 HoaDon co trangThai GIO_HANG (moi khach toi da 1 gio - co unique index UX_HoaDon_gioHang).
 * San pham trong gio = cac dong HoaDonChiTiet (moi chi tiet san pham chi 1 dong - UNIQUE hoaDon + ctsp).
 */
public class GioHangService {

    private final KhuyenMaiService khuyenMaiService = new KhuyenMaiService();
    private final PhieuGiamGiaService phieuService = new PhieuGiamGiaService();

    // =====================================================================
    // XEM GIO HANG
    // =====================================================================

    /**
     * Lay gio hang de hien thi. Moi lan mo gio deu LAM MOI GIA theo khuyen mai hien tai va bao neu gia / ton kho doi.
     */
    public GioHangDTO layGioHang(Long maKhach) {
        return GiaoDich.chay(s -> {
            GioHangDTO dto = new GioHangDTO();
            HoaDon gio = timGio(s, maKhach);
            if (gio == null) return dto;               // chua co gio -> gio rong, khong tao moi khi chi xem

            List<HoaDonChiTiet> dong = danhSachDong(s, gio.getMaHoaDon());
            Map<Long, String> loi = lamMoiGia(s, dong, dto.getCanhBao());
            String thongBaoPhieu = tinhLaiTong(s, gio, dong);
            if (thongBaoPhieu != null) dto.getCanhBao().add(thongBaoPhieu);

            for (HoaDonChiTiet d : dong) dto.getDong().add(taoDong(s, d, loi.get(d.getMaHoaDonChiTiet())));
            dto.setTamTinh(gio.getTamTinh());
            dto.setSoTienGiam(gio.getSoTienGiam());
            dto.setTongTien(gio.getTongTien());
            dto.setMaPhieu(gio.getPhieuGiamGia() == null ? null : gio.getPhieuGiamGia().getMaGiamGia());
            dto.setCoLoi(!loi.isEmpty());
            return dto;
        });
    }

    /** Tong so san pham trong gio (hien tren icon gio o header). */
    public int demSoLuong(Long maKhach) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            Long tong = s.createQuery("select sum(d.soLuong) from HoaDonChiTiet d, HoaDon h where d.maHoaDon = h.maHoaDon "
                    + "and h.khachHang.maKhachHang = :k and h.trangThai = 'GIO_HANG'", Long.class)
                    .setParameter("k", maKhach).uniqueResult();
            return tong == null ? 0 : tong.intValue();
        }
    }

    // =====================================================================
    // THEM / SUA / XOA
    // =====================================================================

    public void themSanPham(Long maKhach, Long maChiTietSanPham, int soLuong) {
        if (soLuong <= 0) throw new NghiepVuException("Số lượng phải lớn hơn 0");
        GiaoDich.chay(s -> {
            ChiTietSanPham c = s.get(ChiTietSanPham.class, maChiTietSanPham);
            // note code nhu nay nhe ban: chi them bien the DANG_BAN cua san pham dang hien thi
            if (c == null || !"DANG_BAN".equals(c.getTrangThai()) || !Boolean.TRUE.equals(c.getSanPham().getDangHienThi())) {
                throw new NghiepVuException("Sản phẩm này hiện không bán");
            }
            HoaDon gio = layHoacTaoGio(s, maKhach);
            HoaDonChiTiet dong = timDong(s, gio.getMaHoaDon(), maChiTietSanPham);

            // note code nhu nay nhe ban: da co dong thi CONG DON so luong (unique hoaDon + bien the), khong vuot ton kho
            int tongSoLuong = soLuong + (dong == null ? 0 : dong.getSoLuong());
            if (tongSoLuong > c.getSoLuongTon()) {
                throw new NghiepVuException("Chỉ còn " + c.getSoLuongTon() + " sản phẩm trong kho (trong giỏ bạn đã có "
                        + (dong == null ? 0 : dong.getSoLuong()) + ")");
            }

            // note code nhu nay nhe ban: CHOT GIA tai thoi diem them (donGiaGoc, donGia da ap khuyen mai) cung ten / ma hang / mo ta
            KetQuaGiaDTO gia = khuyenMaiService.tinhGia(s, c, tongSoLuong);
            if (dong == null) {
                dong = new HoaDonChiTiet();
                dong.setMaHoaDon(gio.getMaHoaDon());
                dong.setMaChiTietSanPham(c.getMaChiTietSanPham());
                dong.setTenSanPham(c.getSanPham().getTenSanPham());
                dong.setMaHang(c.getMaHang());
                dong.setMoTaChiTiet(moTaBienThe(c));
            }
            dong.setSoLuong(tongSoLuong);
            dong.setDonGiaGoc(gia.getGiaGoc());
            dong.setDonGia(gia.getGiaSauGiam());
            dong.setMaChiTietChuongTrinhKhuyenMai(gia.getMaChiTietCtkm());
            if (dong.getMaHoaDonChiTiet() == null) s.persist(dong);

            tinhLaiTong(s, gio, danhSachDong(s, gio.getMaHoaDon()));
            return null;
        });
    }

    /** soLuong <= 0 thi xoa dong. */
    public void capNhatSoLuong(Long maKhach, Long maHoaDonChiTiet, int soLuong) {
        GiaoDich.chay(s -> {
            HoaDon gio = timGioBatBuoc(s, maKhach);
            HoaDonChiTiet dong = layDongCuaGio(s, gio, maHoaDonChiTiet);
            if (soLuong <= 0) {
                s.remove(dong);
            } else {
                ChiTietSanPham c = s.get(ChiTietSanPham.class, dong.getMaChiTietSanPham());
                if (c == null || soLuong > c.getSoLuongTon()) {
                    throw new NghiepVuException("Chỉ còn " + (c == null ? 0 : c.getSoLuongTon()) + " sản phẩm trong kho");
                }
                KetQuaGiaDTO gia = khuyenMaiService.tinhGia(s, c, soLuong);   // doi so luong co the doi dieu kien khuyen mai
                dong.setSoLuong(soLuong);
                dong.setDonGiaGoc(gia.getGiaGoc());
                dong.setDonGia(gia.getGiaSauGiam());
                dong.setMaChiTietChuongTrinhKhuyenMai(gia.getMaChiTietCtkm());
            }
            s.flush();
            tinhLaiTong(s, gio, danhSachDong(s, gio.getMaHoaDon()));
            return null;
        });
    }

    public void xoaDong(Long maKhach, Long maHoaDonChiTiet) {
        capNhatSoLuong(maKhach, maHoaDonChiTiet, 0);
    }

    public void xoaGio(Long maKhach) {
        GiaoDich.chay(s -> {
            HoaDon gio = timGio(s, maKhach);
            if (gio == null) return null;
            for (HoaDonChiTiet d : danhSachDong(s, gio.getMaHoaDon())) s.remove(d);
            s.flush();
            gio.setPhieuGiamGia(null);
            tinhLaiTong(s, gio, new ArrayList<HoaDonChiTiet>());
            return null;
        });
    }

    // =====================================================================
    // PHIEU GIAM GIA
    // =====================================================================

    public void apDungPhieu(Long maKhach, String ma) {
        GiaoDich.chay(s -> {
            HoaDon gio = timGioBatBuoc(s, maKhach);
            List<HoaDonChiTiet> dong = danhSachDong(s, gio.getMaHoaDon());
            if (dong.isEmpty()) throw new NghiepVuException("Giỏ hàng đang trống");
            tinhLaiTong(s, gio, dong);       // tinh lai tamTinh moi nhat truoc khi kiem tra phieu

            KetQuaPhieuDTO kq = phieuService.kiemTraVaTinhGiam(s, ma, maKhach, gio.getTamTinh());
            if (!kq.isHopLe()) throw new NghiepVuException(kq.getLyDoTuChoi());
            gio.setPhieuGiamGia(kq.getPhieu());
            tinhLaiTong(s, gio, dong);
            return null;
        });
    }

    public void boPhieu(Long maKhach) {
        GiaoDich.chay(s -> {
            HoaDon gio = timGioBatBuoc(s, maKhach);
            gio.setPhieuGiamGia(null);
            tinhLaiTong(s, gio, danhSachDong(s, gio.getMaHoaDon()));
            return null;
        });
    }

    // =====================================================================
    // HAM DUNG CHUNG (public de DatHangService dung lai)
    // =====================================================================

    public HoaDon timGio(Session s, Long maKhach) {
        return s.createQuery("from HoaDon h where h.khachHang.maKhachHang = :k and h.trangThai = 'GIO_HANG'", HoaDon.class)
                .setParameter("k", maKhach).uniqueResult();
    }

    private HoaDon timGioBatBuoc(Session s, Long maKhach) {
        HoaDon gio = timGio(s, maKhach);
        if (gio == null) throw new NghiepVuException("Giỏ hàng của bạn đang trống");
        return gio;
    }

    // note code nhu nay nhe ban: chua co gio thi tao moi + ghi LichSu (null -> GIO_HANG) nhu du lieu mau
    private HoaDon layHoacTaoGio(Session s, Long maKhach) {
        HoaDon gio = timGio(s, maKhach);
        if (gio != null) return gio;

        KhachHang kh = s.get(KhachHang.class, maKhach);
        if (kh == null) throw new NghiepVuException("Không tìm thấy khách hàng");
        gio = new HoaDon();
        gio.setKhachHang(kh);
        gio.setTamTinh(BigDecimal.ZERO);
        gio.setSoTienGiam(BigDecimal.ZERO);
        gio.setTongTien(BigDecimal.ZERO);
        gio.setTrangThai("GIO_HANG");
        s.persist(gio);

        LichSuTrangThaiHoaDon ls = new LichSuTrangThaiHoaDon();
        ls.setMaHoaDon(gio.getMaHoaDon());
        ls.setTuTrangThai(null);
        ls.setDenTrangThai("GIO_HANG");
        ls.setGhiChu("Khách hàng tạo giỏ hàng");
        ls.setNgayThayDoi(LocalDateTime.now());
        s.persist(ls);
        // Neu 2 request cung tao gio 1 luc thi unique index se bao loi o day (khach thu lai la duoc)
        return gio;
    }

    public List<HoaDonChiTiet> danhSachDong(Session s, Long maHoaDon) {
        return s.createQuery("from HoaDonChiTiet d where d.maHoaDon = :h order by d.maHoaDonChiTiet", HoaDonChiTiet.class)
                .setParameter("h", maHoaDon).list();
    }

    private HoaDonChiTiet timDong(Session s, Long maHoaDon, Long maChiTietSanPham) {
        return s.createQuery("from HoaDonChiTiet d where d.maHoaDon = :h and d.maChiTietSanPham = :c", HoaDonChiTiet.class)
                .setParameter("h", maHoaDon).setParameter("c", maChiTietSanPham).uniqueResult();
    }

    // note code nhu nay nhe ban: chi cho sua dong thuoc gio cua CHINH khach do (chong sua gio nguoi khac bang cach doi id tren form)
    private HoaDonChiTiet layDongCuaGio(Session s, HoaDon gio, Long maHoaDonChiTiet) {
        HoaDonChiTiet dong = maHoaDonChiTiet == null ? null : s.get(HoaDonChiTiet.class, maHoaDonChiTiet);
        if (dong == null || !dong.getMaHoaDon().equals(gio.getMaHoaDon())) throw new NghiepVuException("Không tìm thấy sản phẩm trong giỏ");
        return dong;
    }

    /**
     * Lam moi gia + kiem tra tung dong. Tra ve Map {maHoaDonChiTiet -> loi} cho cac dong KHONG dat hang duoc
     * (ngung ban, het hang, ton khong du). Gia doi thi cap nhat va them thong bao vao canhBao.
     */
    public Map<Long, String> lamMoiGia(Session s, List<HoaDonChiTiet> dong, List<String> canhBao) {
        Map<Long, String> loi = new HashMap<Long, String>();
        for (HoaDonChiTiet d : dong) {
            ChiTietSanPham c = s.get(ChiTietSanPham.class, d.getMaChiTietSanPham());
            String ten = d.getTenSanPham() + " (" + d.getMaHang() + ")";

            if (c == null || !"DANG_BAN".equals(c.getTrangThai()) || !Boolean.TRUE.equals(c.getSanPham().getDangHienThi())) {
                loi.put(d.getMaHoaDonChiTiet(), ten + " hiện không còn bán, vui lòng xóa khỏi giỏ");
                continue;
            }
            if (c.getSoLuongTon() < d.getSoLuong()) {
                loi.put(d.getMaHoaDonChiTiet(), ten + " chỉ còn " + c.getSoLuongTon() + " sản phẩm trong kho, vui lòng giảm số lượng");
                continue;
            }
            KetQuaGiaDTO gia = khuyenMaiService.tinhGia(s, c, d.getSoLuong());
            if (gia.getGiaSauGiam().compareTo(d.getDonGia()) != 0) {
                canhBao.add("Giá " + ten + " đã thay đổi: " + DinhDangUtil.tien(d.getDonGia()) + " → " + DinhDangUtil.tien(gia.getGiaSauGiam()));
            }
            d.setDonGiaGoc(gia.getGiaGoc());
            d.setDonGia(gia.getGiaSauGiam());
            d.setMaChiTietChuongTrinhKhuyenMai(gia.getMaChiTietCtkm());
        }
        return loi;
    }

    /**
     * Tinh lai tamTinh, soTienGiam, tongTien cua gio.
     * Phieu khong con hop le (doi gio, het han...) thi tu go ra va tra ve thong bao (null = khong co gi bao).
     */
    public String tinhLaiTong(Session s, HoaDon gio, List<HoaDonChiTiet> dong) {
        BigDecimal tamTinh = BigDecimal.ZERO;
        for (HoaDonChiTiet d : dong) tamTinh = tamTinh.add(d.getDonGia().multiply(BigDecimal.valueOf(d.getSoLuong())));
        gio.setTamTinh(tamTinh);

        BigDecimal giam = BigDecimal.ZERO;
        String thongBao = null;
        if (gio.getPhieuGiamGia() != null) {
            KetQuaPhieuDTO kq = phieuService.kiemTra(s, gio.getPhieuGiamGia(), gio.getKhachHang().getMaKhachHang(), tamTinh);
            if (kq.isHopLe()) {
                giam = kq.getSoTienGiam();
            } else {
                thongBao = "Mã giảm giá " + gio.getPhieuGiamGia().getMaGiamGia() + " đã được gỡ: " + kq.getLyDoTuChoi();
                gio.setPhieuGiamGia(null);
            }
        }
        // note code nhu nay nhe ban: CHECK trong SQL bat buoc tongTien = tamTinh - soTienGiam va soTienGiam <= tamTinh
        gio.setSoTienGiam(giam);
        gio.setTongTien(tamTinh.subtract(giam));
        return thongBao;
    }

    private DongGioHangDTO taoDong(Session s, HoaDonChiTiet d, String loi) {
        DongGioHangDTO x = new DongGioHangDTO();
        x.setMaHoaDonChiTiet(d.getMaHoaDonChiTiet());
        x.setMaChiTietSanPham(d.getMaChiTietSanPham());
        x.setTenSanPham(d.getTenSanPham());
        x.setMaHang(d.getMaHang());
        x.setMoTaChiTiet(d.getMoTaChiTiet());
        x.setGiaGoc(d.getDonGiaGoc());
        x.setGiaHienTai(d.getDonGia());
        x.setSoLuong(d.getSoLuong());
        x.setThanhTien(d.getDonGia().multiply(BigDecimal.valueOf(d.getSoLuong())));
        x.setLoi(loi);

        ChiTietSanPham c = s.get(ChiTietSanPham.class, d.getMaChiTietSanPham());
        if (c != null) {
            x.setTonKho(c.getSoLuongTon());
            List<HinhAnhSanPham> anh = s.createQuery("from HinhAnhSanPham h where h.maSanPham = :sp and h.laAnhChinh = true", HinhAnhSanPham.class)
                    .setParameter("sp", c.getSanPham().getMaSanPham()).setMaxResults(1).list();
            if (!anh.isEmpty()) x.setAnh(anh.get(0).getDuongDanAnh());
        }
        return x;
    }

    /** Mo ta bien the, dung dinh dang cua du lieu mau: "Xanh duong, 225 g, Soi carbon, chu vi tay cam 4.125 inch". */
    public static String moTaBienThe(ChiTietSanPham c) {
        List<String> phan = new ArrayList<String>();
        if (c.getMauSac() != null) phan.add(c.getMauSac().getTenMauSac());
        if (c.getKhoiLuong() != null) phan.add(c.getKhoiLuong().getKhoiLuongGram() + " g");
        if (c.getChatLieu() != null) phan.add(c.getChatLieu().getTenChatLieu());
        if (c.getChuViTayCam() != null) phan.add("chu vi tay cầm " + c.getChuViTayCam().getChuViInch().toPlainString() + " inch");
        return String.join(", ", phan);
    }
}
