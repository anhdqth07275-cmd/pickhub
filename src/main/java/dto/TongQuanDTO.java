package dto;

import java.math.BigDecimal;
import java.util.List;
import entity.ChiTietSanPham;
import entity.HoaDon;

/** Toan bo du lieu cho trang Dashboard. */
public class TongQuanDTO {
    // 4 the so tren cung
    public BigDecimal doanhThuThang = BigDecimal.ZERO;
    public long donMoi;
    public long soKhachHang;
    // khoi "Can xu ly"
    public long donChoXacNhan;
    public long donDangGiao;
    public long tonKhoCanhBao;
    public long khuyenMaiSapChay;
    // bieu do
    public List<CotBieuDoDTO> bieuDo;
    public String kieuBieuDo;               // "ngay" hoac "thang"
    // bang
    public List<HoaDon> donGanDay;
    public List<ChiTietSanPham> tonKhoThap;
    public List<Object[]> topBanChay;       // {tenSanPham, tongSoLuong}
    public List<OThongKeDTO> thongKeBang;   // 18 bang

    // getter cho EL (JSP doc duoc ${tq.donMoi})
    public BigDecimal getDoanhThuThang() { return doanhThuThang; }
    public long getDonMoi() { return donMoi; }
    public long getSoKhachHang() { return soKhachHang; }
    public long getDonChoXacNhan() { return donChoXacNhan; }
    public long getDonDangGiao() { return donDangGiao; }
    public long getTonKhoCanhBao() { return tonKhoCanhBao; }
    public long getKhuyenMaiSapChay() { return khuyenMaiSapChay; }
    public List<CotBieuDoDTO> getBieuDo() { return bieuDo; }
    public String getKieuBieuDo() { return kieuBieuDo; }
    public List<HoaDon> getDonGanDay() { return donGanDay; }
    public List<ChiTietSanPham> getTonKhoThap() { return tonKhoThap; }
    public List<Object[]> getTopBanChay() { return topBanChay; }
    public List<OThongKeDTO> getThongKeBang() { return thongKeBang; }
}
