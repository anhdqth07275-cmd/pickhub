package repository;

import entity.PhieuGiamGia;


/**
 * Repository cua bang PhieuGiamGia. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class PhieuGiamGiaRepository extends BaseRepository<PhieuGiamGia> {

    public PhieuGiamGiaRepository() {
        super(PhieuGiamGia.class, "maPhieuGiamGia");
    }
}
