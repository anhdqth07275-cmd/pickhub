package dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/** 1 dong trong gio hang de hien thi. loi != null nghia la dong nay khong dat hang duoc (het hang / ngung ban). */
@Getter
@Setter
public class DongGioHangDTO {
    private Long maHoaDonChiTiet;
    private Long maChiTietSanPham;
    private String tenSanPham;
    private String maHang;
    private String moTaChiTiet;
    private BigDecimal giaGoc;
    private BigDecimal giaHienTai;
    private int soLuong;
    private BigDecimal thanhTien;
    private int tonKho;
    private String anh;
    private String loi;

    public boolean isCoGiamGia() {
        return giaGoc != null && giaHienTai != null && giaHienTai.compareTo(giaGoc) < 0;
    }
}
