package com.pickhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "HoaDonChiTiet")
public class HoaDonChiTiet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHoaDonChiTiet;

    private Long maHoaDon;

    private Long maChiTietSanPham;

    private Long maChiTietChuongTrinhKhuyenMai;

    private String tenSanPham;

    private String maHang;

    private String moTaChiTiet;

    private BigDecimal donGiaGoc;

    private BigDecimal donGia;

    private Integer soLuong;

    // cot tinh toan trong DB = donGia * soLuong, chi doc
    @Column(insertable = false, updatable = false)
    private BigDecimal thanhTien;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaHoaDonChiTiet() {
        return maHoaDonChiTiet;
    }

    public void setMaHoaDonChiTiet(Long maHoaDonChiTiet) {
        this.maHoaDonChiTiet = maHoaDonChiTiet;
    }

    public Long getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Long maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public Long getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public void setMaChiTietSanPham(Long maChiTietSanPham) {
        this.maChiTietSanPham = maChiTietSanPham;
    }

    public Long getMaChiTietChuongTrinhKhuyenMai() {
        return maChiTietChuongTrinhKhuyenMai;
    }

    public void setMaChiTietChuongTrinhKhuyenMai(Long maChiTietChuongTrinhKhuyenMai) {
        this.maChiTietChuongTrinhKhuyenMai = maChiTietChuongTrinhKhuyenMai;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getMaHang() {
        return maHang;
    }

    public void setMaHang(String maHang) {
        this.maHang = maHang;
    }

    public String getMoTaChiTiet() {
        return moTaChiTiet;
    }

    public void setMoTaChiTiet(String moTaChiTiet) {
        this.moTaChiTiet = moTaChiTiet;
    }

    public BigDecimal getDonGiaGoc() {
        return donGiaGoc;
    }

    public void setDonGiaGoc(BigDecimal donGiaGoc) {
        this.donGiaGoc = donGiaGoc;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}
