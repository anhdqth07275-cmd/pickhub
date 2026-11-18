package com.pickhub.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "ThuongHieu")
public class ThuongHieu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maThuongHieu;

    private String tenThuongHieu;

    private String duongDan;

    private String duongDanLogo;

    private String trangThai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaThuongHieu() {
        return maThuongHieu;
    }

    public void setMaThuongHieu(Long maThuongHieu) {
        this.maThuongHieu = maThuongHieu;
    }

    public String getTenThuongHieu() {
        return tenThuongHieu;
    }

    public void setTenThuongHieu(String tenThuongHieu) {
        this.tenThuongHieu = tenThuongHieu;
    }

    public String getDuongDan() {
        return duongDan;
    }

    public void setDuongDan(String duongDan) {
        this.duongDan = duongDan;
    }

    public String getDuongDanLogo() {
        return duongDanLogo;
    }

    public void setDuongDanLogo(String duongDanLogo) {
        this.duongDanLogo = duongDanLogo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
