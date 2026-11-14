package repository;

import entity.ChuViTayCam;

import java.util.List;

/**
 * Repository cua bang ChuViTayCam. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class ChuViTayCamRepository extends BaseRepository<ChuViTayCam> {

    public ChuViTayCamRepository() {
        super(ChuViTayCam.class, "maChuViTayCam");
    }

    // note code nhu nay nhe ban: chi lay muc HOAT_DONG de do vao dropdown form san pham (muc NGUNG khong cho chon nua)
    public List<ChuViTayCam> layDangHoatDong() {
        return layCoDieuKien("x.trangThai = 'HOAT_DONG'", "x.chuViInch asc", 1000);
    }
}
