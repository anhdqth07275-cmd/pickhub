package com.pickhub.dto;

import java.math.BigDecimal;

/** So lieu tong ket ca ban: so hoa don + doanh thu (chi tinh hoa don HOAN_THANH ban tai quay). */
public class TongKetNgayDTO {

    private final long soHoaDon;
    private final BigDecimal doanhThu;

    public TongKetNgayDTO(long soHoaDon, BigDecimal doanhThu) {
        this.soHoaDon = soHoaDon;
        this.doanhThu = doanhThu;
    }

    public long getSoHoaDon() {
        return soHoaDon;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }
}
