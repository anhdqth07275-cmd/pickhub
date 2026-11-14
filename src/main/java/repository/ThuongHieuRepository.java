package repository;

import entity.ThuongHieu;


/**
 * Repository cua bang ThuongHieu. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class ThuongHieuRepository extends BaseRepository<ThuongHieu> {

    public ThuongHieuRepository() {
        super(ThuongHieu.class, "maThuongHieu");
    }
}
