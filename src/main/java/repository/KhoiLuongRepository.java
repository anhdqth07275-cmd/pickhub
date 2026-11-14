package repository;

import entity.KhoiLuong;


/**
 * Repository cua bang KhoiLuong. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class KhoiLuongRepository extends BaseRepository<KhoiLuong> {

    public KhoiLuongRepository() {
        super(KhoiLuong.class, "maKhoiLuong");
    }
}
