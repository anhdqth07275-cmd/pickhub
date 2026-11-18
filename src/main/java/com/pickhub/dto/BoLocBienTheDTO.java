package com.pickhub.dto;

import java.math.BigDecimal;

/**
 * Bo loc san pham cho man hinh ban hang (loc o muc BIEN THE - moi bien the la 1 mat hang co ma hang rieng).
 * Truong nao null / rong thi khong loc theo truong do; cac dieu kien duoc KET HOP bang AND.
 */
public class BoLocBienTheDTO {

    private String tuKhoa;              // nhieu tu: moi tu phai khop ten san pham, ma hang HOAC ten thuong hieu
    private Long maDanhMuc;             // chon danh muc cha thi lay ca san pham thuoc danh muc con
    private Long maThuongHieu;
    private String loaiSanPham;         // VOT / BONG / PHU_KIEN / TUI
    private Long maMauSac;
    private Long maKhoiLuong;
    private Long maChatLieu;
    private Long maChuViTayCam;
    private BigDecimal giaTu;
    private BigDecimal giaDen;
    private String tinhTrangTon = "CON_HANG";   // CON_HANG (mac dinh) / SAP_HET / HET_HANG / TAT_CA
    private boolean goiYKhuyenMai;      // true: chi hien mat hang dang co khuyen mai
    private String sapXep = "ten";      // ten / moi / gia_tang / gia_giam / ton_it / ton_nhieu
    private int trang = 1;
    private int kichThuoc = 24;

    public String getTuKhoa() {
        return tuKhoa;
    }

    public void setTuKhoa(String tuKhoa) {
        this.tuKhoa = tuKhoa;
    }

    public Long getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(Long maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public Long getMaThuongHieu() {
        return maThuongHieu;
    }

    public void setMaThuongHieu(Long maThuongHieu) {
        this.maThuongHieu = maThuongHieu;
    }

    public String getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(String loaiSanPham) {
        this.loaiSanPham = loaiSanPham;
    }

    public Long getMaMauSac() {
        return maMauSac;
    }

    public void setMaMauSac(Long maMauSac) {
        this.maMauSac = maMauSac;
    }

    public Long getMaKhoiLuong() {
        return maKhoiLuong;
    }

    public void setMaKhoiLuong(Long maKhoiLuong) {
        this.maKhoiLuong = maKhoiLuong;
    }

    public Long getMaChatLieu() {
        return maChatLieu;
    }

    public void setMaChatLieu(Long maChatLieu) {
        this.maChatLieu = maChatLieu;
    }

    public Long getMaChuViTayCam() {
        return maChuViTayCam;
    }

    public void setMaChuViTayCam(Long maChuViTayCam) {
        this.maChuViTayCam = maChuViTayCam;
    }

    public BigDecimal getGiaTu() {
        return giaTu;
    }

    public void setGiaTu(BigDecimal giaTu) {
        this.giaTu = giaTu;
    }

    public BigDecimal getGiaDen() {
        return giaDen;
    }

    public void setGiaDen(BigDecimal giaDen) {
        this.giaDen = giaDen;
    }

    public String getTinhTrangTon() {
        return tinhTrangTon;
    }

    public void setTinhTrangTon(String tinhTrangTon) {
        this.tinhTrangTon = tinhTrangTon;
    }

    public boolean isGoiYKhuyenMai() {
        return goiYKhuyenMai;
    }

    public void setGoiYKhuyenMai(boolean goiYKhuyenMai) {
        this.goiYKhuyenMai = goiYKhuyenMai;
    }

    public String getSapXep() {
        return sapXep;
    }

    public void setSapXep(String sapXep) {
        this.sapXep = sapXep;
    }

    public int getTrang() {
        return trang;
    }

    public void setTrang(int trang) {
        this.trang = Math.max(trang, 1);
    }

    public int getKichThuoc() {
        return kichThuoc;
    }

    public void setKichThuoc(int kichThuoc) {
        this.kichThuoc = Math.min(Math.max(kichThuoc, 1), 100);
    }
}
