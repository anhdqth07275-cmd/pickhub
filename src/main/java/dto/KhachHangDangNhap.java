package dto;

import java.io.Serializable;

/** Thong tin khach hang luu trong session (KHONG luu mat khau). */
public class KhachHangDangNhap implements Serializable {
    private final Long id;
    private final String hoTen;
    private final String email;
    private final String soDienThoai;

    public KhachHangDangNhap(Long id, String hoTen, String email, String soDienThoai) {
        this.id = id; this.hoTen = hoTen; this.email = email; this.soDienThoai = soDienThoai;
    }
    public Long getId() { return id; }
    public String getHoTen() { return hoTen; }
    public String getEmail() { return email; }
    public String getSoDienThoai() { return soDienThoai; }
}
