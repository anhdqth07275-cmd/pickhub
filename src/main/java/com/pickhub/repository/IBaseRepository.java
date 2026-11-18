package com.pickhub.repository;

import com.pickhub.entity.ThuongHieu;
import com.pickhub.entity.DanhMuc;
import com.pickhub.entity.MauSac;
import com.pickhub.entity.ChuViTayCam;
import com.pickhub.entity.KhoiLuong;
import com.pickhub.entity.ChatLieu;
import com.pickhub.entity.NhanVien;
import com.pickhub.entity.KhachHang;
import com.pickhub.entity.SanPham;
import com.pickhub.entity.ChiTietSanPham;
import com.pickhub.entity.HinhAnhSanPham;
import com.pickhub.entity.ChuongTrinhKhuyenMai;
import com.pickhub.entity.ChiTietChuongTrinhKhuyenMai;
import com.pickhub.entity.PhieuGiamGia;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.HoaDonChiTiet;
import com.pickhub.entity.LichSuTrangThaiHoaDon;
import com.pickhub.entity.SuDungPhieuGiamGia;

import java.util.List;

/**
 * BASE CRUD INTERFACE cua tang Repository: day du ham them / sua / xoa / tim / dem cho TUNG entity.
 * Khong dung generic - moi entity co ham rieng, ten ham ghi ro entity (vd layTatCaSanPham, themSanPham...).
 * Cac repository chuyen biet (vd IChiTietSanPhamRepository) ke thua interface nay va bo sung truy van rieng.
 */
public interface IBaseRepository {

    // ---------- ThuongHieu ----------
    List<ThuongHieu> layTatCaThuongHieu();
    ThuongHieu timThuongHieuTheoId(Long id);
    ThuongHieu themThuongHieu(ThuongHieu thuongHieu);
    ThuongHieu capNhatThuongHieu(ThuongHieu thuongHieu);
    void xoaThuongHieu(Long id);
    long demThuongHieu();

    // ---------- DanhMuc ----------
    List<DanhMuc> layTatCaDanhMuc();
    DanhMuc timDanhMucTheoId(Long id);
    DanhMuc themDanhMuc(DanhMuc danhMuc);
    DanhMuc capNhatDanhMuc(DanhMuc danhMuc);
    void xoaDanhMuc(Long id);
    long demDanhMuc();

    // ---------- MauSac ----------
    List<MauSac> layTatCaMauSac();
    MauSac timMauSacTheoId(Long id);
    MauSac themMauSac(MauSac mauSac);
    MauSac capNhatMauSac(MauSac mauSac);
    void xoaMauSac(Long id);
    long demMauSac();

    // ---------- ChuViTayCam ----------
    List<ChuViTayCam> layTatCaChuViTayCam();
    ChuViTayCam timChuViTayCamTheoId(Long id);
    ChuViTayCam themChuViTayCam(ChuViTayCam chuViTayCam);
    ChuViTayCam capNhatChuViTayCam(ChuViTayCam chuViTayCam);
    void xoaChuViTayCam(Long id);
    long demChuViTayCam();

    // ---------- KhoiLuong ----------
    List<KhoiLuong> layTatCaKhoiLuong();
    KhoiLuong timKhoiLuongTheoId(Long id);
    KhoiLuong themKhoiLuong(KhoiLuong khoiLuong);
    KhoiLuong capNhatKhoiLuong(KhoiLuong khoiLuong);
    void xoaKhoiLuong(Long id);
    long demKhoiLuong();

    // ---------- ChatLieu ----------
    List<ChatLieu> layTatCaChatLieu();
    ChatLieu timChatLieuTheoId(Long id);
    ChatLieu themChatLieu(ChatLieu chatLieu);
    ChatLieu capNhatChatLieu(ChatLieu chatLieu);
    void xoaChatLieu(Long id);
    long demChatLieu();

    // ---------- NhanVien ----------
    List<NhanVien> layTatCaNhanVien();
    NhanVien timNhanVienTheoId(Long id);
    NhanVien themNhanVien(NhanVien nhanVien);
    NhanVien capNhatNhanVien(NhanVien nhanVien);
    void xoaNhanVien(Long id);
    long demNhanVien();

    // ---------- KhachHang ----------
    List<KhachHang> layTatCaKhachHang();
    KhachHang timKhachHangTheoId(Long id);
    KhachHang themKhachHang(KhachHang khachHang);
    KhachHang capNhatKhachHang(KhachHang khachHang);
    void xoaKhachHang(Long id);
    long demKhachHang();

    // ---------- SanPham ----------
    List<SanPham> layTatCaSanPham();
    SanPham timSanPhamTheoId(Long id);
    SanPham themSanPham(SanPham sanPham);
    SanPham capNhatSanPham(SanPham sanPham);
    void xoaSanPham(Long id);
    long demSanPham();

    // ---------- ChiTietSanPham ----------
    List<ChiTietSanPham> layTatCaChiTietSanPham();
    ChiTietSanPham timChiTietSanPhamTheoId(Long id);
    ChiTietSanPham themChiTietSanPham(ChiTietSanPham chiTietSanPham);
    ChiTietSanPham capNhatChiTietSanPham(ChiTietSanPham chiTietSanPham);
    void xoaChiTietSanPham(Long id);
    long demChiTietSanPham();

    // ---------- HinhAnhSanPham ----------
    List<HinhAnhSanPham> layTatCaHinhAnhSanPham();
    HinhAnhSanPham timHinhAnhSanPhamTheoId(Long id);
    HinhAnhSanPham themHinhAnhSanPham(HinhAnhSanPham hinhAnhSanPham);
    HinhAnhSanPham capNhatHinhAnhSanPham(HinhAnhSanPham hinhAnhSanPham);
    void xoaHinhAnhSanPham(Long id);
    long demHinhAnhSanPham();

    // ---------- ChuongTrinhKhuyenMai ----------
    List<ChuongTrinhKhuyenMai> layTatCaChuongTrinhKhuyenMai();
    ChuongTrinhKhuyenMai timChuongTrinhKhuyenMaiTheoId(Long id);
    ChuongTrinhKhuyenMai themChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai);
    ChuongTrinhKhuyenMai capNhatChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai);
    void xoaChuongTrinhKhuyenMai(Long id);
    long demChuongTrinhKhuyenMai();

    // ---------- ChiTietChuongTrinhKhuyenMai ----------
    List<ChiTietChuongTrinhKhuyenMai> layTatCaChiTietChuongTrinhKhuyenMai();
    ChiTietChuongTrinhKhuyenMai timChiTietChuongTrinhKhuyenMaiTheoId(Long id);
    ChiTietChuongTrinhKhuyenMai themChiTietChuongTrinhKhuyenMai(ChiTietChuongTrinhKhuyenMai chiTietChuongTrinhKhuyenMai);
    ChiTietChuongTrinhKhuyenMai capNhatChiTietChuongTrinhKhuyenMai(ChiTietChuongTrinhKhuyenMai chiTietChuongTrinhKhuyenMai);
    void xoaChiTietChuongTrinhKhuyenMai(Long id);
    long demChiTietChuongTrinhKhuyenMai();

    // ---------- PhieuGiamGia ----------
    List<PhieuGiamGia> layTatCaPhieuGiamGia();
    PhieuGiamGia timPhieuGiamGiaTheoId(Long id);
    PhieuGiamGia themPhieuGiamGia(PhieuGiamGia phieuGiamGia);
    PhieuGiamGia capNhatPhieuGiamGia(PhieuGiamGia phieuGiamGia);
    void xoaPhieuGiamGia(Long id);
    long demPhieuGiamGia();

    // ---------- HoaDon ----------
    List<HoaDon> layTatCaHoaDon();
    HoaDon timHoaDonTheoId(Long id);
    HoaDon themHoaDon(HoaDon hoaDon);
    HoaDon capNhatHoaDon(HoaDon hoaDon);
    void xoaHoaDon(Long id);
    long demHoaDon();

    // ---------- HoaDonChiTiet ----------
    List<HoaDonChiTiet> layTatCaHoaDonChiTiet();
    HoaDonChiTiet timHoaDonChiTietTheoId(Long id);
    HoaDonChiTiet themHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet);
    HoaDonChiTiet capNhatHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet);
    void xoaHoaDonChiTiet(Long id);
    long demHoaDonChiTiet();

    // ---------- LichSuTrangThaiHoaDon ----------
    List<LichSuTrangThaiHoaDon> layTatCaLichSuTrangThaiHoaDon();
    LichSuTrangThaiHoaDon timLichSuTrangThaiHoaDonTheoId(Long id);
    LichSuTrangThaiHoaDon themLichSuTrangThaiHoaDon(LichSuTrangThaiHoaDon lichSuTrangThaiHoaDon);
    LichSuTrangThaiHoaDon capNhatLichSuTrangThaiHoaDon(LichSuTrangThaiHoaDon lichSuTrangThaiHoaDon);
    void xoaLichSuTrangThaiHoaDon(Long id);
    long demLichSuTrangThaiHoaDon();

    // ---------- SuDungPhieuGiamGia ----------
    List<SuDungPhieuGiamGia> layTatCaSuDungPhieuGiamGia();
    SuDungPhieuGiamGia timSuDungPhieuGiamGiaTheoId(Long id);
    SuDungPhieuGiamGia themSuDungPhieuGiamGia(SuDungPhieuGiamGia suDungPhieuGiamGia);
    SuDungPhieuGiamGia capNhatSuDungPhieuGiamGia(SuDungPhieuGiamGia suDungPhieuGiamGia);
    void xoaSuDungPhieuGiamGia(Long id);
    long demSuDungPhieuGiamGia();
}
