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

    // ---- tim THEO SAN PHAM (the san pham tren man hinh ban hang) ----

    /** Ma cac SAN PHAM (khong lap) cua trang hien tai: san pham co it nhat 1 bien the khop bo loc. Phan trang theo SAN PHAM. */
    List<Long> timMaSanPham(BoLocBienTheDTO boLoc);

    /** Tong so SAN PHAM khop bo loc (de tinh so trang). */
    long demSanPham(BoLocBienTheDTO boLoc);

    /** TAT CA bien the chua ngung ban cua cac san pham (ke ca bien the het hang) de popup chon bien the hien du. */
    List<ChiTietSanPham> timBienTheTheoSanPham(List<Long> danhSachMaSanPham);
}
