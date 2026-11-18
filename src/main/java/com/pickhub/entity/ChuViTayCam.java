package com.pickhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChuViTayCam")
public class ChuViTayCam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChuViTayCam;

    private BigDecimal chuViInch;

    private String trangThai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaChuViTayCam() {
        return maChuViTayCam;
    }

    public void setMaChuViTayCam(Long maChuViTayCam) {
        this.maChuViTayCam = maChuViTayCam;
    }

    public BigDecimal getChuViInch() {
        return chuViInch;
    }

    public void setChuViInch(BigDecimal chuViInch) {
        this.chuViInch = chuViInch;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
