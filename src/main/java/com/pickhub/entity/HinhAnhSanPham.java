package com.pickhub.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "HinhAnhSanPham")
public class HinhAnhSanPham extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHinhAnhSanPham;

    private Long maSanPham;

    private Long maChiTietSanPham;

    private String duongDanAnh;

    private String loaiTep;

    private Integer dungLuongKb;

    private Boolean laAnhChinh;

    private Integer thuTuSapXep;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaHinhAnhSanPham() {
        return maHinhAnhSanPham;
    }

    public void setMaHinhAnhSanPham(Long maHinhAnhSanPham) {
        this.maHinhAnhSanPham = maHinhAnhSanPham;
    }

    public Long getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(Long maSanPham) {
        this.maSanPham = maSanPham;
    }

    public Long getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public void setMaChiTietSanPham(Long maChiTietSanPham) {
        this.maChiTietSanPham = maChiTietSanPham;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }

    public String getLoaiTep() {
        return loaiTep;
    }

    public void setLoaiTep(String loaiTep) {
        this.loaiTep = loaiTep;
    }

    public Integer getDungLuongKb() {
        return dungLuongKb;
    }

    public void setDungLuongKb(Integer dungLuongKb) {
        this.dungLuongKb = dungLuongKb;
    }

    public Boolean getLaAnhChinh() {
        return laAnhChinh;
    }

    public void setLaAnhChinh(Boolean laAnhChinh) {
        this.laAnhChinh = laAnhChinh;
    }

    public Integer getThuTuSapXep() {
        return thuTuSapXep;
    }

    public void setThuTuSapXep(Integer thuTuSapXep) {
        this.thuTuSapXep = thuTuSapXep;
    }
}
