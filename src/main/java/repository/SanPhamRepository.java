package repository;

import entity.SanPham;


/**
 * Repository cua bang SanPham. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class SanPhamRepository extends BaseRepository<SanPham> {

    public SanPhamRepository() {
        super(SanPham.class, "maSanPham");
    }
}
