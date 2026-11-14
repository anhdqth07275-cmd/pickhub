package repository;

import entity.ChuViTayCam;


/**
 * Repository cua bang ChuViTayCam. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class ChuViTayCamRepository extends BaseRepository<ChuViTayCam> {

    public ChuViTayCamRepository() {
        super(ChuViTayCam.class, "maChuViTayCam");
    }
}
