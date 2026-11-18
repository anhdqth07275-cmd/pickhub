package com.pickhub.dto;

import java.math.BigDecimal;

/** 1 dong trong danh sach hoa don ban tai quay (da dinh dang san de hien thi). */
public class HoaDonQuayDongDTO {

    private Long maHoaDon;
    private String maTraCuu;
    private String thoiGian;
    private String nhanVien;
    private String khachHang;
    private String phuongThuc;
    private int soMatHang;
    private BigDecimal tongTien;
    private String trangThai;

    public Long getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(Long maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaTraCuu() {
        return maTraCuu;
    }

    public void setMaTraCuu(String maTraCuu) {
        this.maTraCuu = maTraCuu;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(String nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(String khachHang) {
        this.khachHang = khachHang;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public int getSoMatHang() {
        return soMatHang;
    }

    public void setSoMatHang(int soMatHang) {
        this.soMatHang = soMatHang;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
