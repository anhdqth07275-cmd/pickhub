package dto;

import java.math.BigDecimal;

/** 1 dong cua bieu do co cau doanh thu theo danh muc. */
public class CoCauDanhMucDTO {
    private final String ten;
    private final BigDecimal giaTri;
    private final int phanTram;      // ty trong tren tong doanh thu (0-100)

    public CoCauDanhMucDTO(String ten, BigDecimal giaTri, int phanTram) {
        this.ten = ten; this.giaTri = giaTri; this.phanTram = phanTram;
    }
    public String getTen() { return ten; }
    public BigDecimal getGiaTri() { return giaTri; }
    public int getPhanTram() { return phanTram; }
}