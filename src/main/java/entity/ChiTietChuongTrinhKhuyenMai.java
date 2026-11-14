package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietChuongTrinhKhuyenMai")
@Getter
@Setter
@NoArgsConstructor
public class ChiTietChuongTrinhKhuyenMai extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChiTietChuongTrinhKhuyenMai;

    private Long maChuongTrinhKhuyenMai;

    @ManyToOne
    @JoinColumn(name = "maChiTietSanPham")
    private ChiTietSanPham chiTietSanPham;

    private String loaiGiam;

    private BigDecimal giaTriGiam;

    private Integer soLuongGioiHan;

    private Integer soLuongDaBan;
}
