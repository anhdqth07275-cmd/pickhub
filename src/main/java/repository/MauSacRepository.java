package repository;

import entity.MauSac;

import java.util.List;

/**
 * Repository cua bang MauSac. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class MauSacRepository extends BaseRepository<MauSac> {

    public MauSacRepository() {
        super(MauSac.class, "maMauSac");
    }

    // note code nhu nay nhe ban: chi lay muc HOAT_DONG de do vao dropdown form san pham (muc NGUNG khong cho chon nua)
    public List<MauSac> layDangHoatDong() {
        return layCoDieuKien("x.trangThai = 'HOAT_DONG'", "x.tenMauSac asc", 1000);
    }
}
