package com.pickhub.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "KhoiLuong")
public class KhoiLuong extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKhoiLuong;

    private Integer khoiLuongGram;

    private String trangThai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaKhoiLuong() {
        return maKhoiLuong;
    }

    public void setMaKhoiLuong(Long maKhoiLuong) {
        this.maKhoiLuong = maKhoiLuong;
    }

    public Integer getKhoiLuongGram() {
        return khoiLuongGram;
    }

    public void setKhoiLuongGram(Integer khoiLuongGram) {
        this.khoiLuongGram = khoiLuongGram;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
