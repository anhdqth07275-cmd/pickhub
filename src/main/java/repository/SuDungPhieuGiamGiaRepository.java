package repository;

import entity.SuDungPhieuGiamGia;


/**
 * Repository cua bang SuDungPhieuGiamGia. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class SuDungPhieuGiamGiaRepository extends BaseRepository<SuDungPhieuGiamGia> {

    public SuDungPhieuGiamGiaRepository() {
        super(SuDungPhieuGiamGia.class, "maSuDungPhieuGiamGia");
    }
}
