package dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** 1 bien the dang ban tren trang cua hang (demo, M3 se thay bang storefront day du). */
@Getter
@Setter
public class BienTheBanDTO {
    private Long maChiTietSanPham;
    private String tenSanPham;
    private String maHang;
    private String moTa;
    private BigDecimal giaGoc;
    private BigDecimal giaSauGiam;
    private int soLuongTon;

    public boolean isCoGiamGia() { return giaSauGiam.compareTo(giaGoc) < 0; }
}
