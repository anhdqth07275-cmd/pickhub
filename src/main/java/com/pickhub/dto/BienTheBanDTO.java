package com.pickhub.dto;

import java.math.BigDecimal;

/** 1 mat hang (bien the) hien tren luoi san pham cua man hinh ban hang: da kem anh va gia sau khuyen mai. */
public class BienTheBanDTO {

    private Long maChiTietSanPham;
    private Long maSanPham;
    private String maHang;
    private String tenSanPham;
    private String thuongHieu;
    private String danhMuc;
    private String moTaBienThe;         // vd "Đen / 225 g / Carbon / 4.25 inch"
    private String anh;                 // duong dan web cua anh
    private BigDecimal giaGoc;
    private BigDecimal giaBan;          // gia sau khuyen mai (bang giaGoc neu khong co khuyen mai)
    private Long maChiTietChuongTrinhKhuyenMai;
    private int soLuongTon;
    private int nguongCanhBao;
    private String trangThai;

    public Long getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public void setMaChiTietSanPham(Long maChiTietSanPham) {
        this.maChiTietSanPham = maChiTietSanPham;
    }

    public Long getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(Long maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getMaHang() {
        return maHang;
    }

    public void setMaHang(String maHang) {
        this.maHang = maHang;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getMoTaBienThe() {
        return moTaBienThe;
    }

    public void setMoTaBienThe(String moTaBienThe) {
        this.moTaBienThe = moTaBienThe;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public BigDecimal getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(BigDecimal giaGoc) {
        this.giaGoc = giaGoc;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
    }

    public Long getMaChiTietChuongTrinhKhuyenMai() {
        return maChiTietChuongTrinhKhuyenMai;
    }

    public void setMaChiTietChuongTrinhKhuyenMai(Long maChiTietChuongTrinhKhuyenMai) {
        this.maChiTietChuongTrinhKhuyenMai = maChiTietChuongTrinhKhuyenMai;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public int getNguongCanhBao() {
        return nguongCanhBao;
    }

    public void setNguongCanhBao(int nguongCanhBao) {
        this.nguongCanhBao = nguongCanhBao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isCoKhuyenMai() {
        return giaBan != null && giaGoc != null && giaBan.compareTo(giaGoc) < 0;
    }

    /** Ghi 1 phan tu JSON cho man hinh ban hang. */
    public String toJson() {
        return "{\"id\":" + maChiTietSanPham
                + ",\"maSanPham\":" + maSanPham
                + ",\"sku\":" + com.pickhub.util.JsonUtil.chuoi(maHang)
                + ",\"ten\":" + com.pickhub.util.JsonUtil.chuoi(tenSanPham)
                + ",\"hieu\":" + com.pickhub.util.JsonUtil.chuoi(thuongHieu)
                + ",\"moTa\":" + com.pickhub.util.JsonUtil.chuoi(moTaBienThe)
                + ",\"anh\":" + com.pickhub.util.JsonUtil.chuoi(anh)
                + ",\"giaGoc\":" + com.pickhub.util.JsonUtil.so(giaGoc)
                + ",\"giaBan\":" + com.pickhub.util.JsonUtil.so(giaBan)
                + ",\"ton\":" + soLuongTon
                + ",\"nguong\":" + nguongCanhBao
                + ",\"trangThai\":" + com.pickhub.util.JsonUtil.chuoi(trangThai)
                + "}";
    }
}
