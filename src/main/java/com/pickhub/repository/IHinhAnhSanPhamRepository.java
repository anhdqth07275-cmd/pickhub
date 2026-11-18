package com.pickhub.repository;

import java.util.List;
import java.util.Map;

/** Repository chuyen biet cho hinh anh san pham. */
public interface IHinhAnhSanPhamRepository extends IBaseRepository {

    /** Anh dai dien (laAnhChinh = true) cua nhieu san pham cung luc: maSanPham -> duong dan anh. */
    Map<Long, String> layAnhChinhTheoSanPham(List<Long> danhSachMaSanPham);

    /** Anh RIENG cua tung bien the (neu co): maChiTietSanPham -> duong dan anh. */
    Map<Long, String> layAnhRiengTheoBienThe(List<Long> danhSachMaBienThe);
}
