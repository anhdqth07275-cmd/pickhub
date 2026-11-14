package repository;

import entity.LichSuTrangThaiHoaDon;
import org.hibernate.Session;
import java.util.List;

/**
 * Repository cua bang LichSuTrangThaiHoaDon. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class LichSuTrangThaiHoaDonRepository extends BaseRepository<LichSuTrangThaiHoaDon> {

    public LichSuTrangThaiHoaDonRepository() {
        super(LichSuTrangThaiHoaDon.class, "maLichSuTrangThaiHoaDon");
    }

    // timeline trang thai cua 1 hoa don, cu den moi
    public List<LichSuTrangThaiHoaDon> timTheoHoaDon(Long maHoaDon) {
        try (Session s = moSession()) {
            return s.createQuery("from LichSuTrangThaiHoaDon l where l.maHoaDon = :id order by l.ngayThayDoi, l.maLichSuTrangThaiHoaDon",
                    LichSuTrangThaiHoaDon.class).setParameter("id", maHoaDon).list();
        }
    }
}
