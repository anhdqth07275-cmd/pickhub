package repository;

import entity.MauSac;


/**
 * Repository cua bang MauSac. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class MauSacRepository extends BaseRepository<MauSac> {

    public MauSacRepository() {
        super(MauSac.class, "maMauSac");
    }
}
