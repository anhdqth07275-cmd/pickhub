package com.pickhub.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietSanPham")
public class ChiTietSanPham extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChiTietSanPham;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    private String maHang;

    @ManyToOne
    @JoinColumn(name = "maMauSac")
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "maKhoiLuong")
    private KhoiLuong khoiLuong;

    @ManyToOne
    @JoinColumn(name = "maChatLieu")
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "maChuViTayCam")
    private ChuViTayCam chuViTayCam;

    private BigDecimal doDayMm;

    private BigDecimal giaGoc;

    private Integer soLuongTon;

    private Integer nguongCanhBaoTonKho;

    private String trangThai;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public void setMaChiTietSanPham(Long maChiTietSanPham) {
        this.maChiTietSanPham = maChiTietSanPham;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public String getMaHang() {
        return maHang;
    }

    public void setMaHang(String maHang) {
        this.maHang = maHang;
    }

    public MauSac getMauSac() {
        return mauSac;
    }

    public void setMauSac(MauSac mauSac) {
        this.mauSac = mauSac;
    }

    public KhoiLuong getKhoiLuong() {
        return khoiLuong;
    }

    public void setKhoiLuong(KhoiLuong khoiLuong) {
        this.khoiLuong = khoiLuong;
    }

    public ChatLieu getChatLieu() {
        return chatLieu;
    }

    public void setChatLieu(ChatLieu chatLieu) {
        this.chatLieu = chatLieu;
    }

    public ChuViTayCam getChuViTayCam() {
        return chuViTayCam;
    }

    public void setChuViTayCam(ChuViTayCam chuViTayCam) {
        this.chuViTayCam = chuViTayCam;
    }

    public BigDecimal getDoDayMm() {
        return doDayMm;
    }

    public void setDoDayMm(BigDecimal doDayMm) {
        this.doDayMm = doDayMm;
    }

    public BigDecimal getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(BigDecimal giaGoc) {
        this.giaGoc = giaGoc;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public Integer getNguongCanhBaoTonKho() {
        return nguongCanhBaoTonKho;
    }

    public void setNguongCanhBaoTonKho(Integer nguongCanhBaoTonKho) {
        this.nguongCanhBaoTonKho = nguongCanhBaoTonKho;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
