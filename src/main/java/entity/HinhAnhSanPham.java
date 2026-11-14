package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "HinhAnhSanPham")
@Getter
@Setter
@NoArgsConstructor
public class HinhAnhSanPham extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHinhAnhSanPham;

    private Long maSanPham;

    private Long maChiTietSanPham;

    private String duongDanAnh;

    private String loaiTep;

    private Integer dungLuongKb;

    private Boolean laAnhChinh;

    private Integer thuTuSapXep;
}
