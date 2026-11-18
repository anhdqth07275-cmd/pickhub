package com.pickhub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhachHang")
public class KhachHang extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKhachHang;

    private String hoTen;

    private String thuDienTu;

    private LocalDateTime ngayXacThucThuDienTu;

    private String soDienThoai;

    private String matKhauMaHoa;

    private String duongDanAnhDaiDien;

    private LocalDate ngaySinh;

    private String trangThai;

    private String lyDoKhoa;

    private LocalDateTime ngayKhoa;

    private Integer soLanDangNhapSai;

    private LocalDateTime ngayDangNhapCuoi;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(Long maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getThuDienTu() {
        return thuDienTu;
    }

    public void setThuDienTu(String thuDienTu) {
        this.thuDienTu = thuDienTu;
    }

    public LocalDateTime getNgayXacThucThuDienTu() {
        return ngayXacThucThuDienTu;
    }

    public void setNgayXacThucThuDienTu(LocalDateTime ngayXacThucThuDienTu) {
        this.ngayXacThucThuDienTu = ngayXacThucThuDienTu;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getMatKhauMaHoa() {
        return matKhauMaHoa;
    }

    public void setMatKhauMaHoa(String matKhauMaHoa) {
        this.matKhauMaHoa = matKhauMaHoa;
    }

    public String getDuongDanAnhDaiDien() {
        return duongDanAnhDaiDien;
    }

    public void setDuongDanAnhDaiDien(String duongDanAnhDaiDien) {
        this.duongDanAnhDaiDien = duongDanAnhDaiDien;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDoKhoa() {
        return lyDoKhoa;
    }

    public void setLyDoKhoa(String lyDoKhoa) {
        this.lyDoKhoa = lyDoKhoa;
    }

    public LocalDateTime getNgayKhoa() {
        return ngayKhoa;
    }

    public void setNgayKhoa(LocalDateTime ngayKhoa) {
        this.ngayKhoa = ngayKhoa;
    }

    public Integer getSoLanDangNhapSai() {
        return soLanDangNhapSai;
    }

    public void setSoLanDangNhapSai(Integer soLanDangNhapSai) {
        this.soLanDangNhapSai = soLanDangNhapSai;
    }

    public LocalDateTime getNgayDangNhapCuoi() {
        return ngayDangNhapCuoi;
    }

    public void setNgayDangNhapCuoi(LocalDateTime ngayDangNhapCuoi) {
        this.ngayDangNhapCuoi = ngayDangNhapCuoi;
    }
}
