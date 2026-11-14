package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuTrangThaiHoaDon")
@Getter
@Setter
@NoArgsConstructor
public class LichSuTrangThaiHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLichSuTrangThaiHoaDon;

    private Long maHoaDon;

    private String tuTrangThai;

    private String denTrangThai;

    private String ghiChu;

    @ManyToOne
    @JoinColumn(name = "maNguoiThayDoi")
    private NhanVien nguoiThayDoi;

    private LocalDateTime ngayThayDoi;
}
