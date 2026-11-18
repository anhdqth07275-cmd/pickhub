package com.pickhub.repository;

import com.pickhub.entity.ChiTietChuongTrinhKhuyenMai;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** Repository chuyen biet cho khuyen mai theo san pham. */
public interface IKhuyenMaiRepository extends IBaseRepository {

    /** Khuyen mai DANG CHAY (dung gio, chuong trinh khong tam dung) cua nhieu bien the: maChiTietSanPham -> dong khuyen mai. */
    Map<Long, ChiTietChuongTrinhKhuyenMai> timKhuyenMaiDangChay(List<Long> danhSachMaBienThe, LocalDateTime bayGio);
}
