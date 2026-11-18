package com.pickhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
public class HoaDon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHoaDon;

    private String maTraCuu;

    @ManyToOne
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    private String tenNguoiNhan;

    private String soDienThoaiNguoiNhan;

    private String tinhThanhGiao;

    private String quanHuyenGiao;

    private String phuongXaGiao;

    private String soNhaDuongGiao;

    private BigDecimal tamTinh;

    private BigDecimal soTienGiam;

    private BigDecimal tongTien;

    @ManyToOne
    @JoinColumn(name = "maPhieuGiamGia")
    private PhieuGiamGia phieuGiamGia;

    private String trangThai;

    private String ghiChu;

    private LocalDateTime ngayDatHang;

    @ManyToOne
    @JoinColumn(name = "maNhanVienXacNhan")
    private NhanVien nhanVienXacNhan;

    private LocalDateTime ngayXacNhan;

    private LocalDateTime ngayGiaoThanhCong;

    private LocalDateTime ngayHuy;

    private String lyDoHuy;

    // ===== Cot BO SUNG cho ban hang tai quay (them bang sql/02_Migration_POS.sql) =====
    @Column(name = "kenhBan")
    private String kenhBan;                 // TAI_QUAY | ONLINE

    @Column(name = "phuongThucThanhToan")
    private String phuongThucThanhToan;     // TIEN_MAT | CHUYEN_KHOAN | THE

    @Column(name = "tienKhachDua")
    private BigDecimal tienKhachDua;

    @Column(name = "tienThua")
    private BigDecimal tienThua;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Long maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaTraCuu() {
        return maTraCuu;
    }

    public void setMaTraCuu(String maTraCuu) {
        this.maTraCuu = maTraCuu;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public String getTenNguoiNhan() {
        return tenNguoiNhan;
    }

    public void setTenNguoiNhan(String tenNguoiNhan) {
        this.tenNguoiNhan = tenNguoiNhan;
    }

    public String getSoDienThoaiNguoiNhan() {
        return soDienThoaiNguoiNhan;
    }

    public void setSoDienThoaiNguoiNhan(String soDienThoaiNguoiNhan) {
        this.soDienThoaiNguoiNhan = soDienThoaiNguoiNhan;
    }

    public String getTinhThanhGiao() {
        return tinhThanhGiao;
    }

    public void setTinhThanhGiao(String tinhThanhGiao) {
        this.tinhThanhGiao = tinhThanhGiao;
    }

    public String getQuanHuyenGiao() {
        return quanHuyenGiao;
    }

    public void setQuanHuyenGiao(String quanHuyenGiao) {
        this.quanHuyenGiao = quanHuyenGiao;
    }

    public String getPhuongXaGiao() {
        return phuongXaGiao;
    }

    public void setPhuongXaGiao(String phuongXaGiao) {
        this.phuongXaGiao = phuongXaGiao;
    }

    public String getSoNhaDuongGiao() {
        return soNhaDuongGiao;
    }

    public void setSoNhaDuongGiao(String soNhaDuongGiao) {
        this.soNhaDuongGiao = soNhaDuongGiao;
    }

    public BigDecimal getTamTinh() {
        return tamTinh;
    }

    public void setTamTinh(BigDecimal tamTinh) {
        this.tamTinh = tamTinh;
    }

    public BigDecimal getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(BigDecimal soTienGiam) {
        this.soTienGiam = soTienGiam;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public PhieuGiamGia getPhieuGiamGia() {
        return phieuGiamGia;
    }

    public void setPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        this.phieuGiamGia = phieuGiamGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getNgayDatHang() {
        return ngayDatHang;
    }

    public void setNgayDatHang(LocalDateTime ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public NhanVien getNhanVienXacNhan() {
        return nhanVienXacNhan;
    }

    public void setNhanVienXacNhan(NhanVien nhanVienXacNhan) {
        this.nhanVienXacNhan = nhanVienXacNhan;
    }

    public LocalDateTime getNgayXacNhan() {
        return ngayXacNhan;
    }

    public void setNgayXacNhan(LocalDateTime ngayXacNhan) {
        this.ngayXacNhan = ngayXacNhan;
    }

    public LocalDateTime getNgayGiaoThanhCong() {
        return ngayGiaoThanhCong;
    }

    public void setNgayGiaoThanhCong(LocalDateTime ngayGiaoThanhCong) {
        this.ngayGiaoThanhCong = ngayGiaoThanhCong;
    }

    public LocalDateTime getNgayHuy() {
        return ngayHuy;
    }

    public void setNgayHuy(LocalDateTime ngayHuy) {
        this.ngayHuy = ngayHuy;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

    public String getKenhBan() {
        return kenhBan;
    }

    public void setKenhBan(String kenhBan) {
        this.kenhBan = kenhBan;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public BigDecimal getTienKhachDua() {
        return tienKhachDua;
    }

    public void setTienKhachDua(BigDecimal tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    public BigDecimal getTienThua() {
        return tienThua;
    }

    public void setTienThua(BigDecimal tienThua) {
        this.tienThua = tienThua;
    }
}
