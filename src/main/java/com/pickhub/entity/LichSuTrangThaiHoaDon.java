package com.pickhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuTrangThaiHoaDon")
public class LichSuTrangThaiHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLichSuTrangThaiHoaDon;

    private Long maHoaDon;

    private String tuTrangThai;

    private String denTrangThai;

    private String ghiChu;

    @ManyToOne
    @JoinColumn(name = "maNguoiThayDoi")
    private NhanVien nguoiThayDoi;

    private LocalDateTime ngayThayDoi;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaLichSuTrangThaiHoaDon() {
        return maLichSuTrangThaiHoaDon;
    }

    public void setMaLichSuTrangThaiHoaDon(Long maLichSuTrangThaiHoaDon) {
        this.maLichSuTrangThaiHoaDon = maLichSuTrangThaiHoaDon;
    }

    public Long getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Long maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getTuTrangThai() {
        return tuTrangThai;
    }

    public void setTuTrangThai(String tuTrangThai) {
        this.tuTrangThai = tuTrangThai;
    }

    public String getDenTrangThai() {
        return denTrangThai;
    }

    public void setDenTrangThai(String denTrangThai) {
        this.denTrangThai = denTrangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public NhanVien getNguoiThayDoi() {
        return nguoiThayDoi;
    }

    public void setNguoiThayDoi(NhanVien nguoiThayDoi) {
        this.nguoiThayDoi = nguoiThayDoi;
    }

    public LocalDateTime getNgayThayDoi() {
        return ngayThayDoi;
    }

    public void setNgayThayDoi(LocalDateTime ngayThayDoi) {
        this.ngayThayDoi = ngayThayDoi;
    }
}
