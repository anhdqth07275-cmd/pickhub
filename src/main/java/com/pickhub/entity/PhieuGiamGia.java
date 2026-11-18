package com.pickhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuGiamGia")
public class PhieuGiamGia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maPhieuGiamGia;

    private String maGiamGia;

    private String tenPhieu;

    private String loaiGiam;

    private BigDecimal giaTriGiam;

    private BigDecimal giamToiDa;

    private BigDecimal giaTriDonToiThieu;

    private Integer gioiHanSuDung;

    private Integer soLanDaSuDung;

    private Integer gioiHanMoiNguoi;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;

    private String trangThai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaPhieuGiamGia() {
        return maPhieuGiamGia;
    }

    public void setMaPhieuGiamGia(Long maPhieuGiamGia) {
        this.maPhieuGiamGia = maPhieuGiamGia;
    }

    public String getMaGiamGia() {
        return maGiamGia;
    }

    public void setMaGiamGia(String maGiamGia) {
        this.maGiamGia = maGiamGia;
    }

    public String getTenPhieu() {
        return tenPhieu;
    }

    public void setTenPhieu(String tenPhieu) {
        this.tenPhieu = tenPhieu;
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

    public BigDecimal getGiamToiDa() {
        return giamToiDa;
    }

    public void setGiamToiDa(BigDecimal giamToiDa) {
        this.giamToiDa = giamToiDa;
    }

    public BigDecimal getGiaTriDonToiThieu() {
        return giaTriDonToiThieu;
    }

    public void setGiaTriDonToiThieu(BigDecimal giaTriDonToiThieu) {
        this.giaTriDonToiThieu = giaTriDonToiThieu;
    }

    public Integer getGioiHanSuDung() {
        return gioiHanSuDung;
    }

    public void setGioiHanSuDung(Integer gioiHanSuDung) {
        this.gioiHanSuDung = gioiHanSuDung;
    }

    public Integer getSoLanDaSuDung() {
        return soLanDaSuDung;
    }

    public void setSoLanDaSuDung(Integer soLanDaSuDung) {
        this.soLanDaSuDung = soLanDaSuDung;
    }

    public Integer getGioiHanMoiNguoi() {
        return gioiHanMoiNguoi;
    }

    public void setGioiHanMoiNguoi(Integer gioiHanMoiNguoi) {
        this.gioiHanMoiNguoi = gioiHanMoiNguoi;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDateTime getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDateTime ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
