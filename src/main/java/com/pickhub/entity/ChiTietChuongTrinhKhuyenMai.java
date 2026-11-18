package com.pickhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietChuongTrinhKhuyenMai")
public class ChiTietChuongTrinhKhuyenMai extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChiTietChuongTrinhKhuyenMai;

    private Long maChuongTrinhKhuyenMai;

    @ManyToOne
    @JoinColumn(name = "maChiTietSanPham")
    private ChiTietSanPham chiTietSanPham;

    private String loaiGiam;

    private BigDecimal giaTriGiam;

    private Integer soLuongGioiHan;

    private Integer soLuongDaBan;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaChiTietChuongTrinhKhuyenMai() {
        return maChiTietChuongTrinhKhuyenMai;
    }

    public void setMaChiTietChuongTrinhKhuyenMai(Long maChiTietChuongTrinhKhuyenMai) {
        this.maChiTietChuongTrinhKhuyenMai = maChiTietChuongTrinhKhuyenMai;
    }

    public Long getMaChuongTrinhKhuyenMai() {
        return maChuongTrinhKhuyenMai;
    }

    public void setMaChuongTrinhKhuyenMai(Long maChuongTrinhKhuyenMai) {
        this.maChuongTrinhKhuyenMai = maChuongTrinhKhuyenMai;
    }

    public ChiTietSanPham getChiTietSanPham() {
        return chiTietSanPham;
    }

    public void setChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        this.chiTietSanPham = chiTietSanPham;
    }

    public String getLoaiGiam() {
        return loaiGiam;
    }

    public void setLoaiGiam(String loaiGiam) {
        this.loaiGiam = loaiGiam;
    }

    public BigDecimal getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(BigDecimal giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public Integer getSoLuongGioiHan() {
        return soLuongGioiHan;
    }

    public void setSoLuongGioiHan(Integer soLuongGioiHan) {
        this.soLuongGioiHan = soLuongGioiHan;
    }

    public Integer getSoLuongDaBan() {
        return soLuongDaBan;
    }

    public void setSoLuongDaBan(Integer soLuongDaBan) {
        this.soLuongDaBan = soLuongDaBan;
    }
}
