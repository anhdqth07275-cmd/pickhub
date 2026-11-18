package com.pickhub.repository;

import com.pickhub.entity.PhieuGiamGia;

/** Repository chuyen biet cho phieu giam gia. */
public interface IPhieuGiamGiaRepository extends IBaseRepository {

    /** Tim phieu theo MA khach nhap (khong phan biet hoa thuong). Khong co thi tra null. */
    PhieuGiamGia timTheoMa(String maGiamGia);
}
