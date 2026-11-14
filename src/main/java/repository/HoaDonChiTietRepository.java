package repository;

import entity.HoaDonChiTiet;
import org.hibernate.Session;
import java.util.List;

/**
 * Repository cua bang HoaDonChiTiet. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class HoaDonChiTietRepository extends BaseRepository<HoaDonChiTiet> {

    public HoaDonChiTietRepository() {
        super(HoaDonChiTiet.class, "maHoaDonChiTiet");
    }

    // cac dong san pham cua 1 hoa don
    public List<HoaDonChiTiet> timTheoHoaDon(Long maHoaDon) {
        try (Session s = moSession()) {
            return s.createQuery("from HoaDonChiTiet c where c.maHoaDon = :id order by c.maHoaDonChiTiet", HoaDonChiTiet.class)
                    .setParameter("id", maHoaDon).list();
        }
    }
}
