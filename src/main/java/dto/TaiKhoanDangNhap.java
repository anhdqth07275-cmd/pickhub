package dto;

import java.io.Serializable;

/**
 * Thong tin nhan vien luu trong session (KHONG luu mat khau).
 */
public class TaiKhoanDangNhap implements Serializable {
    private final Long id;
    private final String hoTen;
    private final String email;
    private final String vaiTro;       // QUAN_TRI / QUAN_LY / NHAN_VIEN
    private final String anhDaiDien;

    public TaiKhoanDangNhap(Long id, String hoTen, String email, String vaiTro, String anhDaiDien) {
        this.id = id; this.hoTen = hoTen; this.email = email; this.vaiTro = vaiTro; this.anhDaiDien = anhDaiDien;
    }

    public Long getId() { return id; }
    public String getHoTen() { return hoTen; }
    public String getEmail() { return email; }
    public String getVaiTro() { return vaiTro; }
    public String getAnhDaiDien() { return anhDaiDien; }

    // chu cai dau cua ho ten de ve avatar tron o sidebar
    public String getChuCaiDau() {
        return hoTen == null || hoTen.isEmpty() ? "?" : hoTen.substring(0, 1).toUpperCase();
    }
}
