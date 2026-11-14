package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SuDungPhieuGiamGia")
@Getter
@Setter
@NoArgsConstructor
public class SuDungPhieuGiamGia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSuDungPhieuGiamGia;

    @ManyToOne
    @JoinColumn(name = "maPhieuGiamGia")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    private Long maHoaDon;

    private BigDecimal soTienGiam;

    private LocalDateTime ngaySuDung;

    private String trangThai;

    private LocalDateTime ngayHoanLai;
}
