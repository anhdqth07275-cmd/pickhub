package com.pickhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ChuongTrinhKhuyenMai")
public class ChuongTrinhKhuyenMai extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChuongTrinhKhuyenMai;

    private String tenChuongTrinh;

    private String moTa;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;

    private String trangThai;

    private Long maNguoiTao;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaChuongTrinhKhuyenMai() {
        return maChuongTrinhKhuyenMai;
    }

    public void setMaChuongTrinhKhuyenMai(Long maChuongTrinhKhuyenMai) {
        this.maChuongTrinhKhuyenMai = maChuongTrinhKhuyenMai;
    }

    public String getTenChuongTrinh() {
        return tenChuongTrinh;
    }

    public void setTenChuongTrinh(String tenChuongTrinh) {
        this.tenChuongTrinh = tenChuongTrinh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
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

    public Long getMaNguoiTao() {
        return maNguoiTao;
    }

    public void setMaNguoiTao(Long maNguoiTao) {
        this.maNguoiTao = maNguoiTao;
    }
}
