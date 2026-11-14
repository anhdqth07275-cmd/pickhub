package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuGiamGia")
@Getter
@Setter
@NoArgsConstructor
public class PhieuGiamGia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maPhieuGiamGia;

    private String maGiamGia;

    private String tenPhieu;

    private String loaiGiam;

    private BigDecimal giaTriGiam;

    private BigDecimal giamToiDa;

    private BigDecimal giaTriDonToiThieu;

    private Integer gioiHanSuDung;

    private Integer soLanDaSuDung;

    private Integer gioiHanMoiNguoi;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;

    private String trangThai;
}
