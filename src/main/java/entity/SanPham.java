package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "SanPham")
@Getter
@Setter
@NoArgsConstructor
public class SanPham extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSanPham;

    @ManyToOne
    @JoinColumn(name = "maDanhMuc")
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "maThuongHieu")
    private ThuongHieu thuongHieu;

    private String tenSanPham;

    private String duongDan;

    private String loaiSanPham;

    private String moTaNgan;

    private String moTa;

    private Boolean dangHienThi;

    private Long maNguoiTao;
}
