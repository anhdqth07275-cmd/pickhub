package com.pickhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SuDungPhieuGiamGia")
public class SuDungPhieuGiamGia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSuDungPhieuGiamGia;

    @ManyToOne
    @JoinColumn(name = "maPhieuGiamGia")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    private Long maHoaDon;

    private BigDecimal soTienGiam;

    private LocalDateTime ngaySuDung;

    private String trangThai;

    private LocalDateTime ngayHoanLai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaSuDungPhieuGiamGia() {
        return maSuDungPhieuGiamGia;
    }

    public void setMaSuDungPhieuGiamGia(Long maSuDungPhieuGiamGia) {
        this.maSuDungPhieuGiamGia = maSuDungPhieuGiamGia;
    }

    public PhieuGiamGia getPhieuGiamGia() {
        return phieuGiamGia;
    }

    public void setPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        this.phieuGiamGia = phieuGiamGia;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public Long getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Long maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public BigDecimal getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(BigDecimal soTienGiam) {
        this.soTienGiam = soTienGiam;
    }

    public LocalDateTime getNgaySuDung() {
        return ngaySuDung;
    }

    public void setNgaySuDung(LocalDateTime ngaySuDung) {
        this.ngaySuDung = ngaySuDung;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayHoanLai() {
        return ngayHoanLai;
    }

    public void setNgayHoanLai(LocalDateTime ngayHoanLai) {
        this.ngayHoanLai = ngayHoanLai;
    }
}
