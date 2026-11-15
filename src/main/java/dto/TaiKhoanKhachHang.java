package dto;

/**
 * Thong tin khach hang dang nhap; khong bao gio dua mat khau vao session.
 * note code nhu nay nhe ban: ke thua KhachHangDangNhap cua nhom de BaseServlet.khachHang() va KhachHangFilter
 * (gio hang / dat hang) doc duoc session "khachHang" du dang nhap bang /dang-nhap hay /tai-khoan/dang-nhap.
 */
public class TaiKhoanKhachHang extends KhachHangDangNhap {
    private final String anhDaiDien;

    public TaiKhoanKhachHang(Long id, String hoTen, String email, String anhDaiDien) {
        super(id, hoTen, email, null);   // soDienThoai khong dung o man hinh nay
        this.anhDaiDien = anhDaiDien;
    }

    public String getAnhDaiDien() { return anhDaiDien; }

    public String getChuCaiDau() {
        return getHoTen() == null || getHoTen().isEmpty() ? "?" : getHoTen().substring(0, 1).toUpperCase();
    }
}
