package com.pickhub.dto;

import com.pickhub.entity.ChiTietSanPham;

import java.util.List;

/** 1 trang ket qua truy van bien the o tang Repository (danh sach thuc the + tong so dong thoa bo loc). */
public class TrangChiTietSanPhamDTO {

    private final List<ChiTietSanPham> danhSach;
    private final long tongSo;

    public TrangChiTietSanPhamDTO(List<ChiTietSanPham> danhSach, long tongSo) {
        this.danhSach = danhSach;
        this.tongSo = tongSo;
    }

    public List<ChiTietSanPham> getDanhSach() {
        return danhSach;
    }

    public long getTongSo() {
        return tongSo;
    }
}
