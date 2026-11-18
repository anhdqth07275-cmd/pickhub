package com.pickhub.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "DanhMuc")
public class DanhMuc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDanhMuc;

    @ManyToOne
    @JoinColumn(name = "maDanhMucCha")
    private DanhMuc danhMucCha;

    private String tenDanhMuc;

    private String moTa;

    private String trangThai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(Long maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public DanhMuc getDanhMucCha() {
        return danhMucCha;
    }

    public void setDanhMucCha(DanhMuc danhMucCha) {
        this.danhMucCha = danhMucCha;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
