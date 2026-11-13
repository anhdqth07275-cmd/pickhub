package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "ChuongTrinhKhuyenMai")
public class ChuongTrinhKhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_khuyen_mai", unique = true, nullable = false)
    private String maKhuyenMai;

    @Column(name = "ten_chuong_trinh", nullable = false)
    private String tenChuongTrinh;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    // Trạng thái: SAP_CHAY, DANG_CHAY, KET_THUC
    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "mo_ta")
    private String moTa;

    // Constructors, Getters & Setters
    public ChuongTrinhKhuyenMai() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMaKhuyenMai() { return maKhuyenMai; }
    public void setMaKhuyenMai(String maKhuyenMai) { this.maKhuyenMai = maKhuyenMai; }
    public String getTenChuongTrinh() { return tenChuongTrinh; }
    public void setTenChuongTrinh(String tenChuongTrinh) { this.tenChuongTrinh = tenChuongTrinh; }
    public LocalDateTime getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDateTime ngayBatDau) { this.ngayBatDau = ngayBatDau; }
    public LocalDateTime getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDateTime ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}