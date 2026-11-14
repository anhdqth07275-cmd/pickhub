package service;

import dto.ThongTinGiaoHangDTO;
import entity.*;
import exception.NghiepVuException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import util.GiaoDich;
import util.HibernateConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Nghiep vu DAT HANG (checkout) - module M5.
 * Dat hang = doi HoaDon tu GIO_HANG sang CHO_XAC_NHAN, tru ton kho, ghi khuyen mai / phieu, sinh maTraCuu, ghi lich su.
 */
public class DatHangService {

    private final GioHangService gioHangService = new GioHangService();
    private final SuDungPhieuService suDungPhieuService = new SuDungPhieuService();

    /** Goi y thong tin giao hang: lay tu don gan nhat cua khach, chua co thi lay tu ho so. */
    public ThongTinGiaoHangDTO layThongTinGoiY(Long maKhach) {
        ThongTinGiaoHangDTO tt = new ThongTinGiaoHangDTO();
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            List<HoaDon> ds = s.createQuery("from HoaDon h where h.khachHang.maKhachHang = :k and h.trangThai <> 'GIO_HANG' order by h.maHoaDon desc", HoaDon.class)
                    .setParameter("k", maKhach).setMaxResults(1).list();
            if (!ds.isEmpty()) {
                HoaDon h = ds.get(0);
                tt.setTenNguoiNhan(h.getTenNguoiNhan());
                tt.setSoDienThoaiNguoiNhan(h.getSoDienThoaiNguoiNhan());
                tt.setTinhThanh(h.getTinhThanhGiao());
                tt.setQuanHuyen(h.getQuanHuyenGiao());
                tt.setPhuongXa(h.getPhuongXaGiao());
                tt.setSoNhaDuong(h.getSoNhaDuongGiao());
            } else {
                KhachHang kh = s.get(KhachHang.class, maKhach);
                tt.setTenNguoiNhan(kh.getHoTen());
                tt.setSoDienThoaiNguoiNhan(kh.getSoDienThoai());
            }
        }
        return tt;
    }

    /**
     * Dat hang. Tra ve maTraCuu cua don. Bat ky loi nao -> rollback toan bo (khong tru ton nua chung).
     */
    public String datHang(Long maKhach, ThongTinGiaoHangDTO tt) {
        kiemTraThongTin(tt);

        // note code nhu nay nhe ban: toan bo trong 1 transaction (GiaoDich.chay). Nem NghiepVuException o dau cung rollback het
        return GiaoDich.chay(s -> {
            HoaDon gio = gioHangService.timGio(s, maKhach);
            if (gio == null) throw new NghiepVuException("Giỏ hàng của bạn đang trống");

            // note code nhu nay nhe ban: KHOA dong gio (PESSIMISTIC_WRITE) de bam dat hang 2 lan / 2 tab khong tao 2 don
            s.refresh(gio, LockMode.PESSIMISTIC_WRITE);
            if (!"GIO_HANG".equals(gio.getTrangThai())) throw new NghiepVuException("Giỏ hàng này đã được đặt");

            List<HoaDonChiTiet> dong = gioHangService.danhSachDong(s, gio.getMaHoaDon());
            if (dong.isEmpty()) throw new NghiepVuException("Giỏ hàng của bạn đang trống");

            // 1) kiem tra lai ton, trang thai ban, gia (chot lai gia moi nhat)
            PhieuGiamGia phieuTruoc = gio.getPhieuGiamGia();
            Map<Long, String> loi = gioHangService.lamMoiGia(s, dong, new ArrayList<String>());
            if (!loi.isEmpty()) throw new NghiepVuException(loi.values().iterator().next());
            gioHangService.tinhLaiTong(s, gio, dong);
            // phieu dang ap ma bi go ra (het han / het luot...) thi bao khach chu khong am tham dat don mat giam gia
            if (phieuTruoc != null && gio.getPhieuGiamGia() == null) {
                throw new NghiepVuException("Mã giảm giá không còn hợp lệ, vui lòng kiểm tra lại giỏ hàng");
            }

            LocalDateTime bayGio = LocalDateTime.now();

            // 2) tru ton kho + tang so luong da ban cua khuyen mai
            for (HoaDonChiTiet d : dong) {
                truTonKho(s, d, bayGio);
                tangLuotKhuyenMai(s, d, bayGio);
            }

            // 3) ghi nhan phieu giam gia (neu co)
            if (gio.getPhieuGiamGia() != null) suDungPhieuService.ghiNhanSuDung(s, gio, bayGio);

            // 4) dien thong tin nhan hang (CHECK CK_HoaDon_thongTinNhan bat buoc day du khi khong con la gio hang)
            gio.setTenNguoiNhan(tt.getTenNguoiNhan());
            gio.setSoDienThoaiNguoiNhan(tt.getSoDienThoaiNguoiNhan());
            gio.setTinhThanhGiao(tt.getTinhThanh());
            gio.setQuanHuyenGiao(tt.getQuanHuyen());
            gio.setPhuongXaGiao(tt.getPhuongXa());
            gio.setSoNhaDuongGiao(tt.getSoNhaDuong());
            gio.setGhiChu(tt.getGhiChu().isEmpty() ? null : tt.getGhiChu());

            // 5) sinh maTraCuu + chuyen GIO_HANG -> CHO_XAC_NHAN
            String maTraCuu = MaTraCuuGenerator.taoMa(gio.getMaHoaDon(), bayGio.toLocalDate());
            gio.setMaTraCuu(maTraCuu);
            gio.setNgayDatHang(bayGio);
            gio.setTrangThai("CHO_XAC_NHAN");

            // 6) ghi lich su trang thai (nguoi thay doi = null vi la khach hang)
            LichSuTrangThaiHoaDon ls = new LichSuTrangThaiHoaDon();
            ls.setMaHoaDon(gio.getMaHoaDon());
            ls.setTuTrangThai("GIO_HANG");
            ls.setDenTrangThai("CHO_XAC_NHAN");
            ls.setGhiChu("Khách hàng đặt hàng");
            ls.setNgayThayDoi(bayGio);
            s.persist(ls);

            // TODO: gui email xac nhan don hang (MailUtil)
            return maTraCuu;
        });
    }

    /** Xem don vua dat (chi xem duoc don cua chinh minh). Tra ve null neu khong co. */
    public HoaDon timDonCuaKhach(Long maKhach, String maTraCuu) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createQuery("from HoaDon h where h.maTraCuu = :m and h.khachHang.maKhachHang = :k", HoaDon.class)
                    .setParameter("m", maTraCuu).setParameter("k", maKhach).uniqueResult();
        }
    }

    // note code nhu nay nhe ban: UPDATE ... WHERE soLuongTon >= sl. 0 dong bi anh huong = co nguoi vua mua truoc -> bao het hang va rollback
    private void truTonKho(Session s, HoaDonChiTiet d, LocalDateTime bayGio) {
        int soDong = s.createQuery("update ChiTietSanPham c set c.soLuongTon = c.soLuongTon - :sl, c.ngayCapNhat = :now "
                + "where c.maChiTietSanPham = :id and c.soLuongTon >= :sl and c.trangThai = 'DANG_BAN'")
                .setParameter("sl", d.getSoLuong()).setParameter("now", bayGio).setParameter("id", d.getMaChiTietSanPham()).executeUpdate();
        if (soDong == 0) throw new NghiepVuException(d.getTenSanPham() + " (" + d.getMaHang() + ") vừa hết hàng, vui lòng kiểm tra lại giỏ");

        // ton ve 0 thi chuyen HET_HANG
        s.createQuery("update ChiTietSanPham c set c.trangThai = 'HET_HANG' where c.maChiTietSanPham = :id and c.soLuongTon = 0")
                .setParameter("id", d.getMaChiTietSanPham()).executeUpdate();
    }

    // tang soLuongDaBan cua khuyen mai, van UPDATE co dieu kien de khong vuot soLuongGioiHan
    private void tangLuotKhuyenMai(Session s, HoaDonChiTiet d, LocalDateTime bayGio) {
        if (d.getMaChiTietChuongTrinhKhuyenMai() == null) return;
        int soDong = s.createQuery("update ChiTietChuongTrinhKhuyenMai k set k.soLuongDaBan = k.soLuongDaBan + :sl, k.ngayCapNhat = :now "
                + "where k.maChiTietChuongTrinhKhuyenMai = :id and (k.soLuongGioiHan is null or k.soLuongDaBan + :sl <= k.soLuongGioiHan)")
                .setParameter("sl", d.getSoLuong()).setParameter("now", bayGio)
                .setParameter("id", d.getMaChiTietChuongTrinhKhuyenMai()).executeUpdate();
        if (soDong == 0) throw new NghiepVuException("Khuyến mãi của " + d.getTenSanPham() + " vừa hết lượt, vui lòng mở lại giỏ hàng để cập nhật giá");
    }

    // note code nhu nay nhe ban: validate form theo do dai cot trong SQL (ten <= 100, SDT <= 15, dia chi <= 100 / 255, ghi chu <= 500)
    private void kiemTraThongTin(ThongTinGiaoHangDTO tt) {
        if (tt.getTenNguoiNhan().isEmpty() || tt.getTenNguoiNhan().length() > 100) throw new NghiepVuException("Tên người nhận bắt buộc và tối đa 100 ký tự");
        if (!tt.getSoDienThoaiNguoiNhan().matches("^0\\d{9,10}$")) throw new NghiepVuException("Số điện thoại không hợp lệ (10-11 số, bắt đầu bằng 0)");
        if (tt.getTinhThanh().isEmpty() || tt.getTinhThanh().length() > 100) throw new NghiepVuException("Vui lòng nhập Tỉnh / Thành phố (tối đa 100 ký tự)");
        if (tt.getQuanHuyen().isEmpty() || tt.getQuanHuyen().length() > 100) throw new NghiepVuException("Vui lòng nhập Quận / Huyện (tối đa 100 ký tự)");
        if (tt.getPhuongXa().isEmpty() || tt.getPhuongXa().length() > 100) throw new NghiepVuException("Vui lòng nhập Phường / Xã (tối đa 100 ký tự)");
        if (tt.getSoNhaDuong().isEmpty() || tt.getSoNhaDuong().length() > 255) throw new NghiepVuException("Vui lòng nhập Số nhà, đường (tối đa 255 ký tự)");
        if (tt.getGhiChu().length() > 500) throw new NghiepVuException("Ghi chú tối đa 500 ký tự");
    }
}
