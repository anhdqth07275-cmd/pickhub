package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "HoaDonChiTiet")
@Getter
@Setter
@NoArgsConstructor
public class HoaDonChiTiet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHoaDonChiTiet;

    private Long maHoaDon;

    private Long maChiTietSanPham;

    private Long maChiTietChuongTrinhKhuyenMai;

    private String tenSanPham;

    private String maHang;

    private String moTaChiTiet;

    private BigDecimal donGiaGoc;

    private BigDecimal donGia;

    private Integer soLuong;

    // cot tinh toan trong DB = donGia * soLuong, chi doc
    @Column(insertable = false, updatable = false)
    private BigDecimal thanhTien;
}
