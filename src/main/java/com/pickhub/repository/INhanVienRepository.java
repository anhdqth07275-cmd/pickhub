package com.pickhub.repository;

import com.pickhub.entity.NhanVien;

/** Repository chuyen biet cho nhan vien. */
public interface INhanVienRepository extends IBaseRepository {

    NhanVien timTheoEmail(String thuDienTu);
}
