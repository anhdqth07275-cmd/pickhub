package repository;

import entity.KhoiLuong;

import java.util.List;

/**
 * Repository cua bang KhoiLuong. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class KhoiLuongRepository extends BaseRepository<KhoiLuong> {

    public KhoiLuongRepository() {
        super(KhoiLuong.class, "maKhoiLuong");
    }

    // note code nhu nay nhe ban: chi lay muc HOAT_DONG de do vao dropdown form san pham (muc NGUNG khong cho chon nua)
    public List<KhoiLuong> layDangHoatDong() {
        return layCoDieuKien("x.trangThai = 'HOAT_DONG'", "x.khoiLuongGram asc", 1000);
    }
}
