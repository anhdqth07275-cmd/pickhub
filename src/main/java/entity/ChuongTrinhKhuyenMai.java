package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "ChuongTrinhKhuyenMai")
@Getter
@Setter
@NoArgsConstructor
public class ChuongTrinhKhuyenMai extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChuongTrinhKhuyenMai;

    private String tenChuongTrinh;

    private String moTa;

    private LocalDateTime ngayBatDau;

    private LocalDateTime ngayKetThuc;

    private String trangThai;

    private Long maNguoiTao;
}
