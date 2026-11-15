package service;

import dto.DongGioHangDTO;
import dto.GioHangDTO;
import dto.KetQuaGiaDTO;
import dto.KetQuaPhieuDTO;
import entity.*;
import exception.NghiepVuException;
import org.hibernate.Session;
import repository.ChiTietChuongTrinhKhuyenMaiRepository;
import repository.HoaDonChiTietRepository;
import repository.HoaDonRepository;
import util.DinhDangUtil;
import util.GiaoDich;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nghiep vu GIO HANG (module M5).
 * Gio hang = 1 HoaDon co trangThai GIO_HANG (moi khach toi da 1 gio - unique index UX_HoaDon_gioHang).
 * San pham trong gio = cac dong HoaDonChiTiet (moi chi tiet san pham chi 1 dong - UNIQUE hoaDon + ctsp).
 *
 * Goi sang module khac:
 *  - M3 (san pham): doc ChiTietSanPham (gia goc, ton kho, trang thai ban); tru ton luc dat hang nam o DatHangService -> ChiTietSanPhamRepository.truTon
 *  - M4 (khuyen mai / phieu): KhuyenMaiService.tinhGiaSauKhuyenMai, PhieuGiamGiaService.kiemTraVaTinhGiam
 *  - M6 (lich su): LichSuTrangThaiService.ghiLichSu
 */
public class GioHangService {

    private final HoaDonRepository hoaDonRepo = new HoaDonRepository();
    private final HoaDonChiTietRepository dongRepo = new HoaDonChiTietRepository();
    private final ChiTietChuongTrinhKhuyenMaiRepository mucGiamRepo = new ChiTietChuongTrinhKhuyenMaiRepository();
    private final KhuyenMaiService khuyenMaiService = new KhuyenMaiService();           // M4
    private final PhieuGiamGiaService phieuService = new PhieuGiamGiaService();         // M4
    private final LichSuTrangThaiService lichSuService = new LichSuTrangThaiService();  // M6

    // =====================================================================
    // R - XEM GIO HANG
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
        return dongRepo.demSoLuongTrongGio(maKhach);
    }

    // =====================================================================
    // C - THEM SAN PHAM VAO GIO
    // =====================================================================

    public void themSanPham(Long maKhach, Long maChiTietSanPham, int soLuong) {
        if (soLuong <= 0) throw new NghiepVuException("Số lượng phải lớn hơn 0");
        if (maChiTietSanPham == null) throw new NghiepVuException("Thiếu mã sản phẩm");

        chayCoThuLai(s -> {
            ChiTietSanPham c = s.get(ChiTietSanPham.class, maChiTietSanPham);        // M3: gia goc, ton kho, trang thai ban
            // note code nhu nay nhe ban: chi them bien the DANG_BAN cua san pham dang hien thi
            if (c == null || !"DANG_BAN".equals(c.getTrangThai()) || !Boolean.TRUE.equals(c.getSanPham().getDangHienThi())) {
                throw new NghiepVuException("Sản phẩm này hiện không bán");
            }

            HoaDon gio = layHoacTaoGioHang(s, maKhach);
            HoaDonChiTiet dong = dongRepo.timDong(s, gio.getMaHoaDon(), maChiTietSanPham);

            // note code nhu nay nhe ban: da co dong thi CONG DON so luong (unique hoaDon + bien the), khong vuot ton kho
            int daCo = dong == null ? 0 : dong.getSoLuong();
            int tongSoLuong = soLuong + daCo;
            if (tongSoLuong > c.getSoLuongTon()) {
                throw new NghiepVuException("Chỉ còn " + c.getSoLuongTon() + " sản phẩm trong kho (trong giỏ bạn đã có " + daCo + ")");
            }

            if (dong == null) {
                // note code nhu nay nhe ban: CHOT GIA tai thoi diem them (ten, ma hang, mo ta, donGiaGoc, donGia da ap khuyen mai)
                dong = new HoaDonChiTiet();
                dong.setMaHoaDon(gio.getMaHoaDon());
                dong.setMaChiTietSanPham(c.getMaChiTietSanPham());
                dong.setTenSanPham(c.getSanPham().getTenSanPham());
                dong.setMaHang(c.getMaHang());
                dong.setMoTaChiTiet(moTaBienThe(c));
            }
            apDungGia(dong, tinhGiaChoSoLuong(c, tongSoLuong, null));
            dong.setSoLuong(tongSoLuong);
            if (dong.getMaHoaDonChiTiet() == null) dongRepo.themDong(s, dong);

            tinhLaiTong(s, gio, danhSachDong(s, gio.getMaHoaDon()));
            return null;
        });
    }

    // =====================================================================
    // U - SUA SO LUONG
    // =====================================================================

    /** soLuong <= 0 thi xoa dong. */
    public void capNhatSoLuong(Long maKhach, Long maHoaDonChiTiet, int soLuong) {
        GiaoDich.chay(s -> {
            HoaDon gio = timGioBatBuoc(s, maKhach);
            HoaDonChiTiet dong = layDongCuaGio(s, gio, maHoaDonChiTiet);

            if (soLuong <= 0) {
                dongRepo.xoaDong(s, dong);
            } else {
                ChiTietSanPham c = s.get(ChiTietSanPham.class, dong.getMaChiTietSanPham());
                if (c == null || soLuong > c.getSoLuongTon()) {
                    throw new NghiepVuException("Chỉ còn " + (c == null ? 0 : c.getSoLuongTon()) + " sản phẩm trong kho");
                }
                apDungGia(dong, tinhGiaChoSoLuong(c, soLuong, null));   // doi so luong co the doi dieu kien khuyen mai
                dong.setSoLuong(soLuong);
            }
            s.flush();
            tinhLaiTong(s, gio, danhSachDong(s, gio.getMaHoaDon()));
            return null;
        });
    }

    // =====================================================================
    // D - XOA DONG / XOA GIO
    // =====================================================================

    public void xoaDong(Long maKhach, Long maHoaDonChiTiet) {
        capNhatSoLuong(maKhach, maHoaDonChiTiet, 0);
    }

    public void xoaGio(Long maKhach) {
        GiaoDich.chay(s -> {
            HoaDon gio = timGio(s, maKhach);
            if (gio == null) return null;
            dongRepo.xoaTatCa(s, gio.getMaHoaDon());
            gio.setPhieuGiamGia(null);                                   // gio rong thi bo luon phieu
            tinhLaiTong(s, gio, new ArrayList<HoaDonChiTiet>());
            return null;
        });
    }

    // =====================================================================
    // PHIEU GIAM GIA (goi M4)
    // =====================================================================

    public void apDungPhieu(Long maKhach, String ma) {
        GiaoDich.chay(s -> {
            HoaDon gio = timGioBatBuoc(s, maKhach);
            List<HoaDonChiTiet> dong = danhSachDong(s, gio.getMaHoaDon());
            if (dong.isEmpty()) throw new NghiepVuException("Giỏ hàng đang trống");
            tinhLaiTong(s, gio, dong);       // tinh lai tamTinh moi nhat truoc khi kiem tra phieu

            // note code nhu nay nhe ban: M4 kiem tra trang thai, thoi gian, tong luot, luot moi nguoi, don toi thieu; sai thi co lyDoTuChoi
            KetQuaPhieuDTO kq = phieuService.kiemTraVaTinhGiam(ma, maKhach, gio.getTamTinh());
            if (!kq.isHopLe()) throw new NghiepVuException(kq.getLyDoTuChoi());

            gio.setPhieuGiamGia(s.get(PhieuGiamGia.class, kq.getPhieu().getMaPhieuGiamGia()));
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
        return hoaDonRepo.timGioHangCuaKhach(s, maKhach);
    }

    public List<HoaDonChiTiet> danhSachDong(Session s, Long maHoaDon) {
        return dongRepo.timTheoHoaDon(s, maHoaDon);
    }

    private HoaDon timGioBatBuoc(Session s, Long maKhach) {
        HoaDon gio = timGio(s, maKhach);
        if (gio == null) throw new NghiepVuException("Giỏ hàng của bạn đang trống");
        return gio;
    }

    // note code nhu nay nhe ban: chua co gio thi tao moi + ghi LichSu (null -> GIO_HANG) nhu du lieu mau
    public HoaDon layHoacTaoGioHang(Session s, Long maKhach) {
        HoaDon gio = timGio(s, maKhach);
        if (gio != null) return gio;

        KhachHang kh = s.get(KhachHang.class, maKhach);
        if (kh == null) throw new NghiepVuException("Không tìm thấy khách hàng");
        gio = hoaDonRepo.taoGioHang(s, kh);
        lichSuService.ghiLichSu(s, gio.getMaHoaDon(), null, "GIO_HANG", "Khách hàng tạo giỏ hàng", null);   // M6
        return gio;
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
            KetQuaGiaDTO gia = tinhGiaChoSoLuong(c, d.getSoLuong(), canhBao);
            if (gia.getGiaSauGiam().compareTo(d.getDonGia()) != 0) {
                canhBao.add("Giá " + ten + " đã thay đổi: " + DinhDangUtil.tien(d.getDonGia()) + " → " + DinhDangUtil.tien(gia.getGiaSauGiam()));
            }
            apDungGia(d, gia);
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
            KetQuaPhieuDTO kq = phieuService.kiemTraVaTinhGiam(
                    gio.getPhieuGiamGia().getMaGiamGia(), gio.getKhachHang().getMaKhachHang(), tamTinh);   // M4
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

    /**
     * Gia 1 bien the khi mua "soLuong" cai: gia M4 (tinhGiaSauKhuyenMai) nhung neu so luong vuot so suat khuyen mai con lai
     * thi tinh GIA GOC (neu khong luc dat hang M4 se bao het suat va khach mo lai gio van thay gia cu -> vong lap loi).
     */
    private KetQuaGiaDTO tinhGiaChoSoLuong(ChiTietSanPham c, int soLuong, List<String> canhBao) {
        KetQuaGiaDTO gia = khuyenMaiService.tinhGiaSauKhuyenMai(c);          // M4
        if (gia.getMaChiTietCTKM() == null) return gia;

        ChiTietChuongTrinhKhuyenMai muc = mucGiamRepo.timTheoId(gia.getMaChiTietCTKM());
        if (muc != null && muc.getSoLuongGioiHan() != null && muc.getSoLuongDaBan() + soLuong > muc.getSoLuongGioiHan()) {
            if (canhBao != null) {
                canhBao.add("Khuyến mãi của " + c.getSanPham().getTenSanPham() + " (" + c.getMaHang() + ") chỉ còn "
                        + (muc.getSoLuongGioiHan() - muc.getSoLuongDaBan()) + " suất, số lượng bạn chọn vượt quá nên tính theo giá gốc");
            }
            return new KetQuaGiaDTO(gia.getGiaGoc(), gia.getGiaGoc(), null);
        }
        return gia;
    }

    // chot gia vao dong hoa don chi tiet: donGiaGoc, donGia, maChiTietChuongTrinhKhuyenMai (CK_HDCT_gia: donGia <= donGiaGoc)
    private void apDungGia(HoaDonChiTiet dong, KetQuaGiaDTO gia) {
        dong.setDonGiaGoc(gia.getGiaGoc());
        dong.setDonGia(gia.getGiaSauGiam());
        dong.setMaChiTietChuongTrinhKhuyenMai(gia.getMaChiTietCTKM());
    }

    // note code nhu nay nhe ban: 2 request cung tao gio (hoac cung them 1 san pham) 1 luc -> vi pham unique index; thu lai 1 lan se thay gio da co
    private <R> R chayCoThuLai(GiaoDich.CongViec<R> congViec) {
        try {
            return GiaoDich.chay(congViec);
        } catch (RuntimeException e) {
            if (laLoiTrungDuLieu(e)) return GiaoDich.chay(congViec);
            throw e;
        }
    }

    private boolean laLoiTrungDuLieu(Throwable e) {
        while (e != null) {
            if (e instanceof org.hibernate.exception.ConstraintViolationException) return true;
            e = e.getCause();
        }
        return false;
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
