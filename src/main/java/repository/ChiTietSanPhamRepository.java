package repository;

import entity.ChiTietSanPham;
import org.hibernate.Session;
import java.util.List;

/**
 * Repository cua bang ChiTietSanPham. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class ChiTietSanPhamRepository extends BaseRepository<ChiTietSanPham> {

    public ChiTietSanPhamRepository() {
        super(ChiTietSanPham.class, "maChiTietSanPham");
    }

    // cac bien the (mau x khoi luong x chat lieu x chu vi) cua 1 san pham
    public List<ChiTietSanPham> timTheoSanPham(Long maSanPham) {
        try (Session s = moSession()) {
            return s.createQuery("from ChiTietSanPham c where c.sanPham.maSanPham = :id order by c.maChiTietSanPham", ChiTietSanPham.class)
                    .setParameter("id", maSanPham).list();
        }
    }
}
