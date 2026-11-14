package repository;

import entity.KhachHang;


/**
 * Repository cua bang KhachHang. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class KhachHangRepository extends BaseRepository<KhachHang> {

    public KhachHangRepository() {
        super(KhachHang.class, "maKhachHang");
    }
}
