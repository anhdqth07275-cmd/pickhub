package dto;

import java.math.BigDecimal;

/**
 * HOP DONG M3 -> M4 / M5: thong tin 1 bien the san pham de ban (them gio hang, tinh khuyen mai, dat hang).
 * M3 chi tra GIA GOC. Gia sau khuyen mai do M4 tinh (KhuyenMaiService.layGiaSauKhuyenMai).
 */
public class ThongTinBanDTO {
    private Long maChiTietSanPham;
    private Long maSanPham;
    private String tenSanPham;
    private String maHang;            // SKU
    private String moTaChiTiet;       // vd "Den / 225g / Carbon / 4.25 inch"
    private BigDecimal giaGoc;
    private Integer soLuongTon;
    private String trangThai;         // DANG_BAN / NGUNG_BAN / HET_HANG
    private String anhChinh;
    private boolean coTheMua;         // true khi san pham dang hien thi + bien the DANG_BAN + con hang

    public Long getMaChiTietSanPham() { return maChiTietSanPham; }
    public void setMaChiTietSanPham(Long v) { this.maChiTietSanPham = v; }
    public Long getMaSanPham() { return maSanPham; }
    public void setMaSanPham(Long v) { this.maSanPham = v; }
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String v) { this.tenSanPham = v; }
    public String getMaHang() { return maHang; }
    public void setMaHang(String v) { this.maHang = v; }
    public String getMoTaChiTiet() { return moTaChiTiet; }
    public void setMoTaChiTiet(String v) { this.moTaChiTiet = v; }
    public BigDecimal getGiaGoc() { return giaGoc; }
    public void setGiaGoc(BigDecimal v) { this.giaGoc = v; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer v) { this.soLuongTon = v; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String v) { this.trangThai = v; }
    public String getAnhChinh() { return anhChinh; }
    public void setAnhChinh(String v) { this.anhChinh = v; }
    public boolean isCoTheMua() { return coTheMua; }
    public void setCoTheMua(boolean v) { this.coTheMua = v; }
}
