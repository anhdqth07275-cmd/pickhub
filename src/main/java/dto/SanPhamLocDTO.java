package dto;

import java.math.BigDecimal;

/**
 * Bo loc rieng cho man hinh san pham (M3): ket hop nhieu dieu kien cung luc.
 * Truong nao de null / rong thi bo qua, khong loc theo truong do.
 */
public class SanPhamLocDTO {
    private String tuKhoa;            // tim theo ten san pham HOAC ma hang (SKU)
    private Long maDanhMuc;
    private Long maThuongHieu;
    private Long maMauSac;
    private Long maKhoiLuong;
    private BigDecimal giaTu;
    private BigDecimal giaDen;
    private Boolean dangHienThi;      // true = dang ban tren web, false = da an
    private String tinhTrangTon;      // HET (ton = 0) / SAP_HET (ton <= nguong) / CON (ton > nguong)
    private String sapXep = "moi";    // moi / ten / gia_tang / gia_giam
    private int trang = 1;
    private int kichThuoc = 10;

    public String getTuKhoa() { return tuKhoa; }
    public void setTuKhoa(String v) { this.tuKhoa = v; }
    public Long getMaDanhMuc() { return maDanhMuc; }
    public void setMaDanhMuc(Long v) { this.maDanhMuc = v; }
    public Long getMaThuongHieu() { return maThuongHieu; }
    public void setMaThuongHieu(Long v) { this.maThuongHieu = v; }
    public Long getMaMauSac() { return maMauSac; }
    public void setMaMauSac(Long v) { this.maMauSac = v; }
    public Long getMaKhoiLuong() { return maKhoiLuong; }
    public void setMaKhoiLuong(Long v) { this.maKhoiLuong = v; }
    public BigDecimal getGiaTu() { return giaTu; }
    public void setGiaTu(BigDecimal v) { this.giaTu = v; }
    public BigDecimal getGiaDen() { return giaDen; }
    public void setGiaDen(BigDecimal v) { this.giaDen = v; }
    public Boolean getDangHienThi() { return dangHienThi; }
    public void setDangHienThi(Boolean v) { this.dangHienThi = v; }
    public String getTinhTrangTon() { return tinhTrangTon; }
    public void setTinhTrangTon(String v) { this.tinhTrangTon = v; }
    public String getSapXep() { return sapXep; }
    public void setSapXep(String v) { this.sapXep = v; }
    public int getTrang() { return trang; }
    public void setTrang(int v) { this.trang = Math.max(v, 1); }
    public int getKichThuoc() { return kichThuoc; }
    public void setKichThuoc(int v) { this.kichThuoc = Math.max(v, 1); }
}
