package repository;

import entity.ChiTietChuongTrinhKhuyenMai;
import org.hibernate.Session;
import java.util.List;

/**
 * Repository cua bang ChiTietChuongTrinhKhuyenMai. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class ChiTietChuongTrinhKhuyenMaiRepository extends BaseRepository<ChiTietChuongTrinhKhuyenMai> {

    public ChiTietChuongTrinhKhuyenMaiRepository() {
        super(ChiTietChuongTrinhKhuyenMai.class, "maChiTietChuongTrinhKhuyenMai");
    }

    // cac chi tiet san pham duoc giam trong 1 chuong trinh khuyen mai
    public List<ChiTietChuongTrinhKhuyenMai> timTheoChuongTrinh(Long maChuongTrinh) {
        try (Session s = moSession()) {
            return s.createQuery("from ChiTietChuongTrinhKhuyenMai c where c.maChuongTrinhKhuyenMai = :id order by c.maChiTietChuongTrinhKhuyenMai",
                    ChiTietChuongTrinhKhuyenMai.class).setParameter("id", maChuongTrinh).list();
        }
    }
}
