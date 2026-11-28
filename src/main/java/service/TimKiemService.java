package service;

import dto.BoLoc;
import dto.PhanTrang;
import entity.HoaDon;
import entity.KhachHang;
import entity.SanPham;
import repository.HoaDonRepository;
import repository.KhachHangRepository;
import repository.SanPhamRepository;

/**
 * Tim kiem toan cuc (Ctrl + K): don hang, khach hang, san pham. Moi nhom lay toi da 5 dong dau.
 * Dung lai BoLoc + BaseRepository.timKiem nen gia tri nguoi dung luon di qua tham so (an toan HQL injection).
 */
public class TimKiemService {

    private static final int TOI_DA = 5;
    private final HoaDonRepository hoaDonRepo = new HoaDonRepository();
    private final KhachHangRepository khachHangRepo = new KhachHangRepository();
    private final SanPhamRepository sanPhamRepo = new SanPhamRepository();

    // note code nhu nay nhe ban: tim don theo ma tra cuu, ten + SDT nguoi nhan, SDT khach dat; khong lay GIO_HANG (chua phai don)
    public PhanTrang<HoaDon> timDonHang(String tuKhoa) {
        return hoaDonRepo.timKiem(new BoLoc()
                .cotTim("maTraCuu,tenNguoiNhan,soDienThoaiNguoiNhan,khachHang.soDienThoai")
                .tuKhoa(tuKhoa)
                .dieuKienThem("x.trangThai <> 'GIO_HANG'")
                .sapXep("x.ngayDatHang desc")
                .kichThuoc(TOI_DA));
    }

    // note code nhu nay nhe ban: bo tai khoan he thong "Khach le tai quay" khoi ket qua
    public PhanTrang<KhachHang> timKhachHang(String tuKhoa) {
        return khachHangRepo.timKiem(new BoLoc()
                .cotTim("hoTen,soDienThoai,thuDienTu")
                .tuKhoa(tuKhoa)
                .dieuKienThem("x.thuDienTu <> 'khachle@pickhub.local'")
                .kichThuoc(TOI_DA));
    }

    public PhanTrang<SanPham> timSanPham(String tuKhoa) {
        return sanPhamRepo.timKiem(new BoLoc()
                .cotTim("tenSanPham")
                .tuKhoa(tuKhoa)
                .kichThuoc(TOI_DA));
    }
}