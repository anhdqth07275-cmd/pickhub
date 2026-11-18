package com.pickhub.dto;

import com.pickhub.entity.HoaDon;

import java.util.List;

/** 1 trang ket qua truy van hoa don o tang Repository. */
public class TrangHoaDonDTO {

    private final List<HoaDon> danhSach;
    private final long tongSo;

    public TrangHoaDonDTO(List<HoaDon> danhSach, long tongSo) {
        this.danhSach = danhSach;
        this.tongSo = tongSo;
    }

    public List<HoaDon> getDanhSach() {
        return danhSach;
    }

    public long getTongSo() {
        return tongSo;
    }
}
