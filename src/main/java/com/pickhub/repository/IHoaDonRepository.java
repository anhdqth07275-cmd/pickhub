package com.pickhub.repository;

import com.pickhub.dto.TrangHoaDonDTO;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.HoaDonChiTiet;

import java.time.LocalDateTime;
import java.util.List;

/** Repository chuyen biet cho hoa don ban tai quay. */
public interface IHoaDonRepository extends IBaseRepository {

    /**
     * Luu 1 hoa don ban tai quay TRONG 1 GIAO DICH DUY NHAT: tru ton kho tung mat hang, them hoa don, cac dong chi tiet, lich su trang thai.
     * Thieu hang o bat ky dong nao -> NghiepVuException va HUY TOAN BO (khong bi tru ton ma khong co hoa don).
     */
    HoaDon luuHoaDonTaiQuay(HoaDon hoaDon, List<HoaDonChiTiet> danhSachDong, Long maNhanVien);

    List<HoaDonChiTiet> timDongTheoHoaDon(Long maHoaDon);

    /** Danh sach hoa don ban tai quay: tim theo ma hoa don / ten khach, loc theo khoang thoi gian va nhan vien. */
    TrangHoaDonDTO timHoaDonTaiQuay(String tuKhoa, LocalDateTime tuNgay, LocalDateTime denNgay, Long maNhanVien, int trang, int kichThuoc);

    /** {soHoaDon, tongDoanhThu} cua cac hoa don HOAN_THANH ban tai quay trong khoang [tuNgay, denNgay). maNhanVien = null: moi nhan vien. */
    Object[] tongKetTaiQuay(LocalDateTime tuNgay, LocalDateTime denNgay, Long maNhanVien);
}
