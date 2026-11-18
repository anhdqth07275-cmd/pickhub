package com.pickhub.service;

import com.pickhub.dto.DongBanDTO;
import com.pickhub.entity.HoaDon;

import java.math.BigDecimal;
import java.util.List;

/** Nghiep vu BAN HANG TAI QUAY (POS): lap hoa don, tru ton kho, ap ma giam gia, tinh tien thua. */
public interface IBanHangTaiQuayService extends IBaseService {

    /**
     * Thanh toan 1 hoa don tai quay. Server TU TINH LAI gia va kiem tra ton kho (khong tin gia gui tu trinh duyet).
     *
     * @param phuongThuc TIEN_MAT / CHUYEN_KHOAN / THE
     * @param tienKhachDua so tien khach dua (bat buoc voi tien mat; cac hinh thuc khac coi nhu tra dung so tien)
     * @return hoa don da luu (da co ma hoa don de in)
     */
    HoaDon thanhToan(Long maNhanVien, List<DongBanDTO> danhSachDong, String maGiamGia, String phuongThuc,
                     BigDecimal tienKhachDua, String tenKhach, String soDienThoaiKhach, String ghiChu);
}
