package com.pickhub.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "MauSac")
public class MauSac extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maMauSac;

    private String tenMauSac;

    private String trangThai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaMauSac() {
        return maMauSac;
    }

    public void setMaMauSac(Long maMauSac) {
        this.maMauSac = maMauSac;
    }

    public String getTenMauSac() {
        return tenMauSac;
    }

    public void setTenMauSac(String tenMauSac) {
        this.tenMauSac = tenMauSac;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
