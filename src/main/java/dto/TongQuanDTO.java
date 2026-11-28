package dto;

import java.math.BigDecimal;
import java.util.List;
import entity.ChiTietSanPham;
import entity.HoaDon;

/** Toan bo du lieu cho trang Dashboard. */
public class TongQuanDTO {
    // cac the so tren cung
    public BigDecimal doanhThuThang = BigDecimal.ZERO;
    public TangTruongDTO tangTruongDoanhThu;
    public long donMoi;
    public long soKhachHang;
    public long khachMoiThang;
    public TangTruongDTO tangTruongKhachHang;
    public BigDecimal giaTriTrungBinhDon = BigDecimal.ZERO;   // doanh thu thang / so don hoan thanh
    public long soDonHoanThanhThang;
    // khoi "Can xu ly"
    public long donChoXacNhan;
    public long donDangGiao;
    public long tonKhoCanhBao;
    public long khuyenMaiSapChay;
    // bieu do
    public List<CotBieuDoDTO> bieuDo;
    public String kieuBieuDo;               // "ngay" hoac "thang"
    public List<CoCauDanhMucDTO> coCauDanhMuc;
    // bang
    public List<HoaDon> donGanDay;
    public List<ChiTietSanPham> tonKhoThap;
    public List<Object[]> topBanChay;       // {tenSanPham, tongSoLuong}

    // getter cho EL (JSP doc duoc ${tq.donMoi})
    public BigDecimal getDoanhThuThang() { return doanhThuThang; }
    public TangTruongDTO getTangTruongDoanhThu() { return tangTruongDoanhThu; }
    public long getDonMoi() { return donMoi; }
    public long getSoKhachHang() { return soKhachHang; }
    public long getKhachMoiThang() { return khachMoiThang; }
    public TangTruongDTO getTangTruongKhachHang() { return tangTruongKhachHang; }
    public BigDecimal getGiaTriTrungBinhDon() { return giaTriTrungBinhDon; }
    public long getSoDonHoanThanhThang() { return soDonHoanThanhThang; }
    public long getDonChoXacNhan() { return donChoXacNhan; }
    public long getDonDangGiao() { return donDangGiao; }
    public long getTonKhoCanhBao() { return tonKhoCanhBao; }
    public long getKhuyenMaiSapChay() { return khuyenMaiSapChay; }
    public List<CotBieuDoDTO> getBieuDo() { return bieuDo; }
    public String getKieuBieuDo() { return kieuBieuDo; }
    public List<CoCauDanhMucDTO> getCoCauDanhMuc() { return coCauDanhMuc; }
    public List<HoaDon> getDonGanDay() { return donGanDay; }
    public List<ChiTietSanPham> getTonKhoThap() { return tonKhoThap; }
    public List<Object[]> getTopBanChay() { return topBanChay; }
    public List<OThongKeDTO> thongKeBang;   // 18 bang (vao khu "bang", cung hang voi donGanDay, tonKhoThap)
    public List<OThongKeDTO> getThongKeBang() { return thongKeBang; }   // vao khu getter
}