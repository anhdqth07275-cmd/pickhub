package service;

import dto.ThongTinGiaoHangDTO;
import entity.*;
import exception.NghiepVuException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import repository.ChiTietSanPhamRepository;
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
    private final ChiTietSanPhamRepository chiTietSanPhamRepo = new ChiTietSanPhamRepository();   // M3: tru ton kho
    private final KhuyenMaiService khuyenMaiService = new KhuyenMaiService();                     // M4: ghi nhan so luong khuyen mai da ban
    private final SuDungPhieuService suDungPhieuService = new SuDungPhieuService();               // M4: ghi nhan luot dung phieu
    private final LichSuTrangThaiService lichSuService = new LichSuTrangThaiService();            // M6: ghi lich su trang thai

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

            // 2) dien thong tin nhan hang + sinh maTraCuu + chuyen GIO_HANG -> CHO_XAC_NHAN
            // note code nhu nay nhe ban: CHECK CK_HoaDon_thongTinNhan bat buoc day du thong tin nhan hang khi khong con la gio hang
            gio.setTenNguoiNhan(tt.getTenNguoiNhan());
            gio.setSoDienThoaiNguoiNhan(tt.getSoDienThoaiNguoiNhan());
            gio.setTinhThanhGiao(tt.getTinhThanh());
            gio.setQuanHuyenGiao(tt.getQuanHuyen());
            gio.setPhuongXaGiao(tt.getPhuongXa());
            gio.setSoNhaDuongGiao(tt.getSoNhaDuong());
            gio.setGhiChu(tt.getGhiChu().isEmpty() ? null : tt.getGhiChu());
            String maTraCuu = MaTraCuuGenerator.taoMa(gio.getMaHoaDon(), bayGio.toLocalDate());
            gio.setMaTraCuu(maTraCuu);
            gio.setNgayDatHang(bayGio);
            gio.setTrangThai("CHO_XAC_NHAN");

            // 3) tru ton kho (M3) + tang so luong khuyen mai da ban (M4) cho tung dong
            for (HoaDonChiTiet d : dong) {
                // note code nhu nay nhe ban: truTon la UPDATE co dieu kien (soLuongTon >= sl). false = vua het hang -> nem loi, ca don rollback
                if (!chiTietSanPhamRepo.truTon(s, d.getMaChiTietSanPham(), d.getSoLuong())) {
                    throw new NghiepVuException(d.getTenSanPham() + " (" + d.getMaHang() + ") vừa hết hàng, vui lòng kiểm tra lại giỏ");
                }
                khuyenMaiService.ghiNhanDaBan(s, d.getMaChiTietChuongTrinhKhuyenMai(), d.getSoLuong());   // dong khong co khuyen mai thi M4 tu bo qua
            }

            // 4) ghi nhan luot dung phieu giam gia (M4). Hoa don khong dung phieu thi M4 tu bo qua
            //    (goi SAU khi da set ngayDatHang vi M4 lay ngaySuDung = ngayDatHang)
            suDungPhieuService.ghiNhanSuDung(s, gio.getMaHoaDon());

            // 5) ghi lich su trang thai (M6). nguoiThayDoi = null vi do khach hang dat
            lichSuService.ghiLichSu(s, gio.getMaHoaDon(), "GIO_HANG", "CHO_XAC_NHAN", "Khách hàng đặt hàng", null);

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
