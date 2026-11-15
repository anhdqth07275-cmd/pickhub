package dto;

import java.io.Serializable;

/** Thong tin khach hang dang nhap; khong bao gio dua mat khau vao session. */
public class TaiKhoanKhachHang implements Serializable {
    private final Long id;
    private final String hoTen;
    private final String email;
    private final String anhDaiDien;
    public TaiKhoanKhachHang(Long id, String hoTen, String email, String anhDaiDien) {
        this.id=id; this.hoTen=hoTen; this.email=email; this.anhDaiDien=anhDaiDien;
    }
    public Long getId(){ return id; }
    public String getHoTen(){ return hoTen; }
    public String getEmail(){ return email; }
    public String getAnhDaiDien(){ return anhDaiDien; }
    public String getChuCaiDau(){ return hoTen==null || hoTen.isEmpty()?"?":hoTen.substring(0,1).toUpperCase(); }
}
