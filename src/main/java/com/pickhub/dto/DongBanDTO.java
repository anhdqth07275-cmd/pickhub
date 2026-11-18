package com.pickhub.dto;

/** 1 dong tren hoa don dang lap: mat hang (bien the) + so luong. Gia KHONG nhan tu trinh duyet, server tu tinh lai. */
public class DongBanDTO {

    private final Long maChiTietSanPham;
    private final int soLuong;

    public DongBanDTO(Long maChiTietSanPham, int soLuong) {
        this.maChiTietSanPham = maChiTietSanPham;
        this.soLuong = soLuong;
    }

    public Long getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }
}
