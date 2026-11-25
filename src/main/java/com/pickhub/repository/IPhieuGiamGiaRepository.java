package com.pickhub.repository;

import com.pickhub.entity.PhieuGiamGia;

import java.time.LocalDateTime;
import java.util.List;

/** Repository chuyen biet cho phieu giam gia. */
public interface IPhieuGiamGiaRepository extends IBaseRepository {

    /** Tim phieu theo MA khach nhap (khong phan biet hoa thuong). Khong co thi tra null. */
    PhieuGiamGia timTheoMa(String maGiamGia);

    /**
     * Cac phieu dang dung duoc vao thoi diem bayGio: khong TAM_DUNG, trong thoi han, con luot tong.
     * (Chua xet gia tri don toi thieu - viec do tinh theo hoa don o Service.)
     */
    List<PhieuGiamGia> layDangKhaDung(LocalDateTime bayGio);
}
