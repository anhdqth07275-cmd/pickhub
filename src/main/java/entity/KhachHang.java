package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhachHang")
@Getter
@Setter
@NoArgsConstructor
public class KhachHang extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKhachHang;

    private String hoTen;

    private String thuDienTu;

    private LocalDateTime ngayXacThucThuDienTu;

    private String soDienThoai;

    private String matKhauMaHoa;

    private String duongDanAnhDaiDien;

    private LocalDate ngaySinh;

    private String trangThai;

    private String lyDoKhoa;

    private LocalDateTime ngayKhoa;

    private Integer soLanDangNhapSai;

    private LocalDateTime ngayDangNhapCuoi;
}
