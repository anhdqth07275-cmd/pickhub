package dto;

import java.math.BigDecimal;

/** 1 cot cua bieu do doanh thu. */
public class CotBieuDoDTO {
    private final String nhan;          // T2, T3... hoac 11/2026
    private final BigDecimal giaTri;
    private final int phanTram;         // chieu cao cot (0-100)
    private final boolean homNay;       // to mau xanh la cho cot hien tai

    public CotBieuDoDTO(String nhan, BigDecimal giaTri, int phanTram, boolean homNay) {
        this.nhan = nhan; this.giaTri = giaTri; this.phanTram = phanTram; this.homNay = homNay;
    }
    public String getNhan() { return nhan; }
    public BigDecimal getGiaTri() { return giaTri; }
    public int getPhanTram() { return phanTram; }
    public boolean isHomNay() { return homNay; }
}
