package repository;

import entity.DanhMuc;


/**
 * Repository cua bang DanhMuc. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class DanhMucRepository extends BaseRepository<DanhMuc> {

    public DanhMucRepository() {
        super(DanhMuc.class, "maDanhMuc");
    }
}
