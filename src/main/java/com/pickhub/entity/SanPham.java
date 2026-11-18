package com.pickhub.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "SanPham")
public class SanPham extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSanPham;

    @ManyToOne
    @JoinColumn(name = "maDanhMuc")
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "maThuongHieu")
    private ThuongHieu thuongHieu;

    private String tenSanPham;

    private String duongDan;

    private String loaiSanPham;

    private String moTaNgan;

    private String moTa;

    private Boolean dangHienThi;

    private Long maNguoiTao;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(Long maSanPham) {
        this.maSanPham = maSanPham;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public ThuongHieu getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(ThuongHieu thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getDuongDan() {
        return duongDan;
    }

    public void setDuongDan(String duongDan) {
        this.duongDan = duongDan;
    }

    public String getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(String loaiSanPham) {
        this.loaiSanPham = loaiSanPham;
    }

    public String getMoTaNgan() {
        return moTaNgan;
    }

    public void setMoTaNgan(String moTaNgan) {
        this.moTaNgan = moTaNgan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public Boolean getDangHienThi() {
        return dangHienThi;
    }

    public void setDangHienThi(Boolean dangHienThi) {
        this.dangHienThi = dangHienThi;
    }

    public Long getMaNguoiTao() {
        return maNguoiTao;
    }

    public void setMaNguoiTao(Long maNguoiTao) {
        this.maNguoiTao = maNguoiTao;
    }
}
