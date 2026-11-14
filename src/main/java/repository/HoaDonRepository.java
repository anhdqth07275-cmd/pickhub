package repository;

import entity.HoaDon;


/**
 * Repository cua bang HoaDon. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class HoaDonRepository extends BaseRepository<HoaDon> {

    public HoaDonRepository() {
        super(HoaDon.class, "maHoaDon");
    }
}
