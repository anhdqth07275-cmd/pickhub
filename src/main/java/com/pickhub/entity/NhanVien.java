package com.pickhub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "NhanVien")
public class NhanVien extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maNhanVien;

    private String vaiTro;

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

    private Long maNguoiTao;

    // ===== getter / setter viet tuong minh (khong dung Lombok) =====
    public Long getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(Long maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getThuDienTu() {
        return thuDienTu;
    }

    public void setThuDienTu(String thuDienTu) {
        this.thuDienTu = thuDienTu;
    }

    public LocalDateTime getNgayXacThucThuDienTu() {
        return ngayXacThucThuDienTu;
    }

    public void setNgayXacThucThuDienTu(LocalDateTime ngayXacThucThuDienTu) {
        this.ngayXacThucThuDienTu = ngayXacThucThuDienTu;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getMatKhauMaHoa() {
        return matKhauMaHoa;
    }

    public void setMatKhauMaHoa(String matKhauMaHoa) {
        this.matKhauMaHoa = matKhauMaHoa;
    }

    public String getDuongDanAnhDaiDien() {
        return duongDanAnhDaiDien;
    }

    public void setDuongDanAnhDaiDien(String duongDanAnhDaiDien) {
        this.duongDanAnhDaiDien = duongDanAnhDaiDien;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLyDoKhoa() {
        return lyDoKhoa;
    }

    public void setLyDoKhoa(String lyDoKhoa) {
        this.lyDoKhoa = lyDoKhoa;
    }

    public LocalDateTime getNgayKhoa() {
        return ngayKhoa;
    }

    public void setNgayKhoa(LocalDateTime ngayKhoa) {
        this.ngayKhoa = ngayKhoa;
    }

    public Integer getSoLanDangNhapSai() {
        return soLanDangNhapSai;
    }

    public void setSoLanDangNhapSai(Integer soLanDangNhapSai) {
        this.soLanDangNhapSai = soLanDangNhapSai;
    }

    public LocalDateTime getNgayDangNhapCuoi() {
        return ngayDangNhapCuoi;
    }

    public void setNgayDangNhapCuoi(LocalDateTime ngayDangNhapCuoi) {
        this.ngayDangNhapCuoi = ngayDangNhapCuoi;
    }

    public Long getMaNguoiTao() {
        return maNguoiTao;
    }

    public void setMaNguoiTao(Long maNguoiTao) {
        this.maNguoiTao = maNguoiTao;
    }
}
