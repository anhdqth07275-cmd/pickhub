package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
public class HoaDon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHoaDon;

    private String maTraCuu;

    @ManyToOne
    @JoinColumn(name = "maKhachHang")
    private KhachHang khachHang;

    private String tenNguoiNhan;

    private String soDienThoaiNguoiNhan;

    private String tinhThanhGiao;

    private String quanHuyenGiao;

    private String phuongXaGiao;

    private String soNhaDuongGiao;

    private BigDecimal tamTinh;

    private BigDecimal soTienGiam;

    private BigDecimal tongTien;

    @ManyToOne
    @JoinColumn(name = "maPhieuGiamGia")
    private PhieuGiamGia phieuGiamGia;

    private String trangThai;

    private String ghiChu;

    private LocalDateTime ngayDatHang;

    @ManyToOne
    @JoinColumn(name = "maNhanVienXacNhan")
    private NhanVien nhanVienXacNhan;

    private LocalDateTime ngayXacNhan;

    private LocalDateTime ngayGiaoThanhCong;

    private LocalDateTime ngayHuy;

    private String lyDoHuy;
}
