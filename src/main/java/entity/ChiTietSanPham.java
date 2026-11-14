package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietSanPham")
@Getter
@Setter
@NoArgsConstructor
public class ChiTietSanPham extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChiTietSanPham;

    @ManyToOne
    @JoinColumn(name = "maSanPham")
    private SanPham sanPham;

    private String maHang;

    @ManyToOne
    @JoinColumn(name = "maMauSac")
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "maKhoiLuong")
    private KhoiLuong khoiLuong;

    @ManyToOne
    @JoinColumn(name = "maChatLieu")
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "maChuViTayCam")
    private ChuViTayCam chuViTayCam;

    private BigDecimal doDayMm;

    private BigDecimal giaGoc;

    private Integer soLuongTon;

    private Integer nguongCanhBaoTonKho;

    private String trangThai;
}
