package com.pickhub.service.impl;

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
import com.pickhub.repository.IBaseRepository;
import com.pickhub.repository.impl.BaseRepository;
import com.pickhub.service.IBaseService;

import java.util.List;

/**
 * BASE SERVICE IMPLEMENT: cai dat IBaseService, chuyen tiep moi ham CRUD xuong BaseRepository.
 * Service chuyen biet (SanPhamServiceImpl, BanHangTaiQuayServiceImpl...) ke thua lop nay.
 */
public class BaseServiceImpl implements IBaseService {

    private final IBaseRepository baseRepository = new BaseRepository();

    // ==================== ThuongHieu ====================
    @Override
    public List<ThuongHieu> layTatCaThuongHieu() {
        return baseRepository.layTatCaThuongHieu();
    }

    @Override
    public ThuongHieu timThuongHieuTheoId(Long id) {
        return baseRepository.timThuongHieuTheoId(id);
    }

    @Override
    public ThuongHieu themThuongHieu(ThuongHieu thuongHieu) {
        return baseRepository.themThuongHieu(thuongHieu);
    }

    @Override
    public ThuongHieu capNhatThuongHieu(ThuongHieu thuongHieu) {
        return baseRepository.capNhatThuongHieu(thuongHieu);
    }

    @Override
    public void xoaThuongHieu(Long id) {
        baseRepository.xoaThuongHieu(id);
    }

    @Override
    public long demThuongHieu() {
        return baseRepository.demThuongHieu();
    }

    // ==================== DanhMuc ====================
    @Override
    public List<DanhMuc> layTatCaDanhMuc() {
        return baseRepository.layTatCaDanhMuc();
    }

    @Override
    public DanhMuc timDanhMucTheoId(Long id) {
        return baseRepository.timDanhMucTheoId(id);
    }

    @Override
    public DanhMuc themDanhMuc(DanhMuc danhMuc) {
        return baseRepository.themDanhMuc(danhMuc);
    }

    @Override
    public DanhMuc capNhatDanhMuc(DanhMuc danhMuc) {
        return baseRepository.capNhatDanhMuc(danhMuc);
    }

    @Override
    public void xoaDanhMuc(Long id) {
        baseRepository.xoaDanhMuc(id);
    }

    @Override
    public long demDanhMuc() {
        return baseRepository.demDanhMuc();
    }

    // ==================== MauSac ====================
    @Override
    public List<MauSac> layTatCaMauSac() {
        return baseRepository.layTatCaMauSac();
    }

    @Override
    public MauSac timMauSacTheoId(Long id) {
        return baseRepository.timMauSacTheoId(id);
    }

    @Override
    public MauSac themMauSac(MauSac mauSac) {
        return baseRepository.themMauSac(mauSac);
    }

    @Override
    public MauSac capNhatMauSac(MauSac mauSac) {
        return baseRepository.capNhatMauSac(mauSac);
    }

    @Override
    public void xoaMauSac(Long id) {
        baseRepository.xoaMauSac(id);
    }

    @Override
    public long demMauSac() {
        return baseRepository.demMauSac();
    }

    // ==================== ChuViTayCam ====================
    @Override
    public List<ChuViTayCam> layTatCaChuViTayCam() {
        return baseRepository.layTatCaChuViTayCam();
    }

    @Override
    public ChuViTayCam timChuViTayCamTheoId(Long id) {
        return baseRepository.timChuViTayCamTheoId(id);
    }

    @Override
    public ChuViTayCam themChuViTayCam(ChuViTayCam chuViTayCam) {
        return baseRepository.themChuViTayCam(chuViTayCam);
    }

    @Override
    public ChuViTayCam capNhatChuViTayCam(ChuViTayCam chuViTayCam) {
        return baseRepository.capNhatChuViTayCam(chuViTayCam);
    }

    @Override
    public void xoaChuViTayCam(Long id) {
        baseRepository.xoaChuViTayCam(id);
    }

    @Override
    public long demChuViTayCam() {
        return baseRepository.demChuViTayCam();
    }

    // ==================== KhoiLuong ====================
    @Override
    public List<KhoiLuong> layTatCaKhoiLuong() {
        return baseRepository.layTatCaKhoiLuong();
    }

    @Override
    public KhoiLuong timKhoiLuongTheoId(Long id) {
        return baseRepository.timKhoiLuongTheoId(id);
    }

    @Override
    public KhoiLuong themKhoiLuong(KhoiLuong khoiLuong) {
        return baseRepository.themKhoiLuong(khoiLuong);
    }

    @Override
    public KhoiLuong capNhatKhoiLuong(KhoiLuong khoiLuong) {
        return baseRepository.capNhatKhoiLuong(khoiLuong);
    }

    @Override
    public void xoaKhoiLuong(Long id) {
        baseRepository.xoaKhoiLuong(id);
    }

    @Override
    public long demKhoiLuong() {
        return baseRepository.demKhoiLuong();
    }

    // ==================== ChatLieu ====================
    @Override
    public List<ChatLieu> layTatCaChatLieu() {
        return baseRepository.layTatCaChatLieu();
    }

    @Override
    public ChatLieu timChatLieuTheoId(Long id) {
        return baseRepository.timChatLieuTheoId(id);
    }

    @Override
    public ChatLieu themChatLieu(ChatLieu chatLieu) {
        return baseRepository.themChatLieu(chatLieu);
    }

    @Override
    public ChatLieu capNhatChatLieu(ChatLieu chatLieu) {
        return baseRepository.capNhatChatLieu(chatLieu);
    }

    @Override
    public void xoaChatLieu(Long id) {
        baseRepository.xoaChatLieu(id);
    }

    @Override
    public long demChatLieu() {
        return baseRepository.demChatLieu();
    }

    // ==================== NhanVien ====================
    @Override
    public List<NhanVien> layTatCaNhanVien() {
        return baseRepository.layTatCaNhanVien();
    }

    @Override
    public NhanVien timNhanVienTheoId(Long id) {
        return baseRepository.timNhanVienTheoId(id);
    }

    @Override
    public NhanVien themNhanVien(NhanVien nhanVien) {
        return baseRepository.themNhanVien(nhanVien);
    }

    @Override
    public NhanVien capNhatNhanVien(NhanVien nhanVien) {
        return baseRepository.capNhatNhanVien(nhanVien);
    }

    @Override
    public void xoaNhanVien(Long id) {
        baseRepository.xoaNhanVien(id);
    }

    @Override
    public long demNhanVien() {
        return baseRepository.demNhanVien();
    }

    // ==================== KhachHang ====================
    @Override
    public List<KhachHang> layTatCaKhachHang() {
        return baseRepository.layTatCaKhachHang();
    }

    @Override
    public KhachHang timKhachHangTheoId(Long id) {
        return baseRepository.timKhachHangTheoId(id);
    }

    @Override
    public KhachHang themKhachHang(KhachHang khachHang) {
        return baseRepository.themKhachHang(khachHang);
    }

    @Override
    public KhachHang capNhatKhachHang(KhachHang khachHang) {
        return baseRepository.capNhatKhachHang(khachHang);
    }

    @Override
    public void xoaKhachHang(Long id) {
        baseRepository.xoaKhachHang(id);
    }

    @Override
    public long demKhachHang() {
        return baseRepository.demKhachHang();
    }

    // ==================== SanPham ====================
    @Override
    public List<SanPham> layTatCaSanPham() {
        return baseRepository.layTatCaSanPham();
    }

    @Override
    public SanPham timSanPhamTheoId(Long id) {
        return baseRepository.timSanPhamTheoId(id);
    }

    @Override
    public SanPham themSanPham(SanPham sanPham) {
        return baseRepository.themSanPham(sanPham);
    }

    @Override
    public SanPham capNhatSanPham(SanPham sanPham) {
        return baseRepository.capNhatSanPham(sanPham);
    }

    @Override
    public void xoaSanPham(Long id) {
        baseRepository.xoaSanPham(id);
    }

    @Override
    public long demSanPham() {
        return baseRepository.demSanPham();
    }

    // ==================== ChiTietSanPham ====================
    @Override
    public List<ChiTietSanPham> layTatCaChiTietSanPham() {
        return baseRepository.layTatCaChiTietSanPham();
    }

    @Override
    public ChiTietSanPham timChiTietSanPhamTheoId(Long id) {
        return baseRepository.timChiTietSanPhamTheoId(id);
    }

    @Override
    public ChiTietSanPham themChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        return baseRepository.themChiTietSanPham(chiTietSanPham);
    }

    @Override
    public ChiTietSanPham capNhatChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        return baseRepository.capNhatChiTietSanPham(chiTietSanPham);
    }

    @Override
    public void xoaChiTietSanPham(Long id) {
        baseRepository.xoaChiTietSanPham(id);
    }

    @Override
    public long demChiTietSanPham() {
        return baseRepository.demChiTietSanPham();
    }

    // ==================== HinhAnhSanPham ====================
    @Override
    public List<HinhAnhSanPham> layTatCaHinhAnhSanPham() {
        return baseRepository.layTatCaHinhAnhSanPham();
    }

    @Override
    public HinhAnhSanPham timHinhAnhSanPhamTheoId(Long id) {
        return baseRepository.timHinhAnhSanPhamTheoId(id);
    }

    @Override
    public HinhAnhSanPham themHinhAnhSanPham(HinhAnhSanPham hinhAnhSanPham) {
        return baseRepository.themHinhAnhSanPham(hinhAnhSanPham);
    }

    @Override
    public HinhAnhSanPham capNhatHinhAnhSanPham(HinhAnhSanPham hinhAnhSanPham) {
        return baseRepository.capNhatHinhAnhSanPham(hinhAnhSanPham);
    }

    @Override
    public void xoaHinhAnhSanPham(Long id) {
        baseRepository.xoaHinhAnhSanPham(id);
    }

    @Override
    public long demHinhAnhSanPham() {
        return baseRepository.demHinhAnhSanPham();
    }

    // ==================== ChuongTrinhKhuyenMai ====================
    @Override
    public List<ChuongTrinhKhuyenMai> layTatCaChuongTrinhKhuyenMai() {
        return baseRepository.layTatCaChuongTrinhKhuyenMai();
    }

    @Override
    public ChuongTrinhKhuyenMai timChuongTrinhKhuyenMaiTheoId(Long id) {
        return baseRepository.timChuongTrinhKhuyenMaiTheoId(id);
    }

    @Override
    public ChuongTrinhKhuyenMai themChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
        return baseRepository.themChuongTrinhKhuyenMai(chuongTrinhKhuyenMai);
    }

    @Override
    public ChuongTrinhKhuyenMai capNhatChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
        return baseRepository.capNhatChuongTrinhKhuyenMai(chuongTrinhKhuyenMai);
    }

    @Override
    public void xoaChuongTrinhKhuyenMai(Long id) {
        baseRepository.xoaChuongTrinhKhuyenMai(id);
    }

    @Override
    public long demChuongTrinhKhuyenMai() {
        return baseRepository.demChuongTrinhKhuyenMai();
    }

    // ==================== ChiTietChuongTrinhKhuyenMai ====================
    @Override
    public List<ChiTietChuongTrinhKhuyenMai> layTatCaChiTietChuongTrinhKhuyenMai() {
        return baseRepository.layTatCaChiTietChuongTrinhKhuyenMai();
    }

    @Override
    public ChiTietChuongTrinhKhuyenMai timChiTietChuongTrinhKhuyenMaiTheoId(Long id) {
        return baseRepository.timChiTietChuongTrinhKhuyenMaiTheoId(id);
    }

    @Override
    public ChiTietChuongTrinhKhuyenMai themChiTietChuongTrinhKhuyenMai(ChiTietChuongTrinhKhuyenMai chiTietChuongTrinhKhuyenMai) {
        return baseRepository.themChiTietChuongTrinhKhuyenMai(chiTietChuongTrinhKhuyenMai);
    }

    @Override
    public ChiTietChuongTrinhKhuyenMai capNhatChiTietChuongTrinhKhuyenMai(ChiTietChuongTrinhKhuyenMai chiTietChuongTrinhKhuyenMai) {
        return baseRepository.capNhatChiTietChuongTrinhKhuyenMai(chiTietChuongTrinhKhuyenMai);
    }

    @Override
    public void xoaChiTietChuongTrinhKhuyenMai(Long id) {
        baseRepository.xoaChiTietChuongTrinhKhuyenMai(id);
    }

    @Override
    public long demChiTietChuongTrinhKhuyenMai() {
        return baseRepository.demChiTietChuongTrinhKhuyenMai();
    }

    // ==================== PhieuGiamGia ====================
    @Override
    public List<PhieuGiamGia> layTatCaPhieuGiamGia() {
        return baseRepository.layTatCaPhieuGiamGia();
    }

    @Override
    public PhieuGiamGia timPhieuGiamGiaTheoId(Long id) {
        return baseRepository.timPhieuGiamGiaTheoId(id);
    }

    @Override
    public PhieuGiamGia themPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        return baseRepository.themPhieuGiamGia(phieuGiamGia);
    }

    @Override
    public PhieuGiamGia capNhatPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        return baseRepository.capNhatPhieuGiamGia(phieuGiamGia);
    }

    @Override
    public void xoaPhieuGiamGia(Long id) {
        baseRepository.xoaPhieuGiamGia(id);
    }

    @Override
    public long demPhieuGiamGia() {
        return baseRepository.demPhieuGiamGia();
    }

    // ==================== HoaDon ====================
    @Override
    public List<HoaDon> layTatCaHoaDon() {
        return baseRepository.layTatCaHoaDon();
    }

    @Override
    public HoaDon timHoaDonTheoId(Long id) {
        return baseRepository.timHoaDonTheoId(id);
    }

    @Override
    public HoaDon themHoaDon(HoaDon hoaDon) {
        return baseRepository.themHoaDon(hoaDon);
    }

    @Override
    public HoaDon capNhatHoaDon(HoaDon hoaDon) {
        return baseRepository.capNhatHoaDon(hoaDon);
    }

    @Override
    public void xoaHoaDon(Long id) {
        baseRepository.xoaHoaDon(id);
    }

    @Override
    public long demHoaDon() {
        return baseRepository.demHoaDon();
    }

    // ==================== HoaDonChiTiet ====================
    @Override
    public List<HoaDonChiTiet> layTatCaHoaDonChiTiet() {
        return baseRepository.layTatCaHoaDonChiTiet();
    }

    @Override
    public HoaDonChiTiet timHoaDonChiTietTheoId(Long id) {
        return baseRepository.timHoaDonChiTietTheoId(id);
    }

    @Override
    public HoaDonChiTiet themHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        return baseRepository.themHoaDonChiTiet(hoaDonChiTiet);
    }

    @Override
    public HoaDonChiTiet capNhatHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        return baseRepository.capNhatHoaDonChiTiet(hoaDonChiTiet);
    }

    @Override
    public void xoaHoaDonChiTiet(Long id) {
        baseRepository.xoaHoaDonChiTiet(id);
    }

    @Override
    public long demHoaDonChiTiet() {
        return baseRepository.demHoaDonChiTiet();
    }

    // ==================== LichSuTrangThaiHoaDon ====================
    @Override
    public List<LichSuTrangThaiHoaDon> layTatCaLichSuTrangThaiHoaDon() {
        return baseRepository.layTatCaLichSuTrangThaiHoaDon();
    }

    @Override
    public LichSuTrangThaiHoaDon timLichSuTrangThaiHoaDonTheoId(Long id) {
        return baseRepository.timLichSuTrangThaiHoaDonTheoId(id);
    }

    @Override
    public LichSuTrangThaiHoaDon themLichSuTrangThaiHoaDon(LichSuTrangThaiHoaDon lichSuTrangThaiHoaDon) {
        return baseRepository.themLichSuTrangThaiHoaDon(lichSuTrangThaiHoaDon);
    }

    @Override
    public LichSuTrangThaiHoaDon capNhatLichSuTrangThaiHoaDon(LichSuTrangThaiHoaDon lichSuTrangThaiHoaDon) {
        return baseRepository.capNhatLichSuTrangThaiHoaDon(lichSuTrangThaiHoaDon);
    }

    @Override
    public void xoaLichSuTrangThaiHoaDon(Long id) {
        baseRepository.xoaLichSuTrangThaiHoaDon(id);
    }

    @Override
    public long demLichSuTrangThaiHoaDon() {
        return baseRepository.demLichSuTrangThaiHoaDon();
    }

    // ==================== SuDungPhieuGiamGia ====================
    @Override
    public List<SuDungPhieuGiamGia> layTatCaSuDungPhieuGiamGia() {
        return baseRepository.layTatCaSuDungPhieuGiamGia();
    }

    @Override
    public SuDungPhieuGiamGia timSuDungPhieuGiamGiaTheoId(Long id) {
        return baseRepository.timSuDungPhieuGiamGiaTheoId(id);
    }

    @Override
    public SuDungPhieuGiamGia themSuDungPhieuGiamGia(SuDungPhieuGiamGia suDungPhieuGiamGia) {
        return baseRepository.themSuDungPhieuGiamGia(suDungPhieuGiamGia);
    }

    @Override
    public SuDungPhieuGiamGia capNhatSuDungPhieuGiamGia(SuDungPhieuGiamGia suDungPhieuGiamGia) {
        return baseRepository.capNhatSuDungPhieuGiamGia(suDungPhieuGiamGia);
    }

    @Override
    public void xoaSuDungPhieuGiamGia(Long id) {
        baseRepository.xoaSuDungPhieuGiamGia(id);
    }

    @Override
    public long demSuDungPhieuGiamGia() {
        return baseRepository.demSuDungPhieuGiamGia();
    }
}
