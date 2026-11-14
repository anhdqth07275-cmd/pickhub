package repository;

import entity.ChuongTrinhKhuyenMai;


/**
 * Repository cua bang ChuongTrinhKhuyenMai. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class ChuongTrinhKhuyenMaiRepository extends BaseRepository<ChuongTrinhKhuyenMai> {

    public ChuongTrinhKhuyenMaiRepository() {
        super(ChuongTrinhKhuyenMai.class, "maChuongTrinhKhuyenMai");
    }
}
