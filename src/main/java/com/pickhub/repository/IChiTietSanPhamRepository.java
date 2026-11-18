package com.pickhub.repository;

import com.pickhub.dto.BoLocBienTheDTO;
import com.pickhub.dto.TrangChiTietSanPhamDTO;
import com.pickhub.entity.ChiTietSanPham;

import java.util.List;

/** Repository chuyen biet cho bien the san pham (mat hang co ma hang rieng). Ke thua CRUD tu IBaseRepository. */
public interface IChiTietSanPhamRepository extends IBaseRepository {

    /** Tim kiem + loc nhieu dieu kien + sap xep + phan trang (xem BoLocBienTheDTO). */
    TrangChiTietSanPhamDTO timKiemBienThe(BoLocBienTheDTO boLoc);

    /** Tim chinh xac theo ma hang (dung khi quet ma vach / go ma hang). */
    ChiTietSanPham timTheoMaHang(String maHang);

    List<ChiTietSanPham> timTheoDanhSachId(List<Long> danhSachMa);
}
