package repository;

import entity.HinhAnhSanPham;
import org.hibernate.Session;
import java.util.List;

/**
 * Repository cua bang HinhAnhSanPham. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class HinhAnhSanPhamRepository extends BaseRepository<HinhAnhSanPham> {

    public HinhAnhSanPhamRepository() {
        super(HinhAnhSanPham.class, "maHinhAnhSanPham");
    }

    public List<HinhAnhSanPham> timTheoSanPham(Long maSanPham) {
        try (Session s = moSession()) {
            return s.createQuery("from HinhAnhSanPham h where h.maSanPham = :id order by h.thuTuSapXep", HinhAnhSanPham.class)
                    .setParameter("id", maSanPham).list();
        }
    }
}
