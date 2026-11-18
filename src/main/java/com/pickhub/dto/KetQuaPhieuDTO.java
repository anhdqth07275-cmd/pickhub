package com.pickhub.dto;

import com.pickhub.entity.PhieuGiamGia;

import java.math.BigDecimal;

/** Ket qua kiem tra 1 ma giam gia: phieu hop le + so tien duoc giam tren tam tinh. */
public class KetQuaPhieuDTO {

    private final PhieuGiamGia phieu;
    private final BigDecimal soTienGiam;

    public KetQuaPhieuDTO(PhieuGiamGia phieu, BigDecimal soTienGiam) {
        this.phieu = phieu;
        this.soTienGiam = soTienGiam;
    }

    public PhieuGiamGia getPhieu() {
        return phieu;
    }

    public BigDecimal getSoTienGiam() {
        return soTienGiam;
    }
}
