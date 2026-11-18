package com.pickhub.service;

import com.pickhub.dto.HoaDonQuayDongDTO;
import com.pickhub.dto.TongKetNgayDTO;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.HoaDonChiTiet;

import java.time.LocalDate;
import java.util.List;

/** Tra cuu hoa don ban tai quay: danh sach, chi tiet de in lai, tong ket ngay. */
public interface IHoaDonService extends IBaseService {

    /** Danh sach hoa don trong khoang ngay (bao gom ca 2 dau). maNhanVien = null: xem cua moi nhan vien. */
    List<HoaDonQuayDongDTO> timHoaDonTaiQuay(String tuKhoa, LocalDate tuNgay, LocalDate denNgay, Long maNhanVien);

    HoaDon timHoaDonTaiQuayTheoId(Long maHoaDon);

    List<HoaDonChiTiet> layDongHoaDon(Long maHoaDon);

    TongKetNgayDTO tongKet(LocalDate tuNgay, LocalDate denNgay, Long maNhanVien);
}
