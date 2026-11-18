package com.pickhub.dto;

import java.math.BigDecimal;

/** Gia ban 1 mat hang sau khi ap khuyen mai dang chay. Khong co khuyen mai: giaBan = giaGoc, maChiTietChuongTrinhKhuyenMai = null. */
public class KetQuaGiaDTO {

    private final BigDecimal giaGoc;
    private final BigDecimal giaBan;
    private final Long maChiTietChuongTrinhKhuyenMai;

    public KetQuaGiaDTO(BigDecimal giaGoc, BigDecimal giaBan, Long maChiTietChuongTrinhKhuyenMai) {
        this.giaGoc = giaGoc;
        this.giaBan = giaBan;
        this.maChiTietChuongTrinhKhuyenMai = maChiTietChuongTrinhKhuyenMai;
    }

    public BigDecimal getGiaGoc() {
        return giaGoc;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public Long getMaChiTietChuongTrinhKhuyenMai() {
        return maChiTietChuongTrinhKhuyenMai;
    }
}
