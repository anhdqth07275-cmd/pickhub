package com.pickhub.repository;

import com.pickhub.entity.KhachHang;

/** Repository chuyen biet cho khach hang (chi dung de lay "khach le tai quay"; quan ly tai khoan khach dang tam dong). */
public interface IKhachHangRepository extends IBaseRepository {

    KhachHang timTheoEmail(String thuDienTu);
}
