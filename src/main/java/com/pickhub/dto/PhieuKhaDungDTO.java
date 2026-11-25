package com.pickhub.dto;

import com.pickhub.entity.PhieuGiamGia;

import java.math.BigDecimal;

/**
 * 1 voucher hien trong popup "Xem va chon voucher" o man hinh POS:
 * phieu + so tien giam UOC TINH tren hoa don hien tai + da du dieu kien ap dung hay chua.
 */
public class PhieuKhaDungDTO {

    private final PhieuGiamGia phieu;
    private final BigDecimal soTienGiam;     // 0 neu chua du dieu kien
    private final boolean duDieuKien;        // tamTinh >= giaTriDonToiThieu
    private final BigDecimal canThemTien;    // con thieu bao nhieu tien hang de du dieu kien (0 neu da du)

    public PhieuKhaDungDTO(PhieuGiamGia phieu, BigDecimal soTienGiam, boolean duDieuKien, BigDecimal canThemTien) {
        this.phieu = phieu;
        this.soTienGiam = soTienGiam;
        this.duDieuKien = duDieuKien;
        this.canThemTien = canThemTien;
    }

    public PhieuGiamGia getPhieu() { return phieu; }
    public BigDecimal getSoTienGiam() { return soTienGiam; }
    public boolean isDuDieuKien() { return duDieuKien; }
    public BigDecimal getCanThemTien() { return canThemTien; }
}
