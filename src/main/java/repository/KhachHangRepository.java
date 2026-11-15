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
    public KhachHang timTheoEmail(String email) {
        try (org.hibernate.Session s = moSession()) {
            return s.createQuery("from KhachHang k where lower(k.thuDienTu)=:e", KhachHang.class)
                    .setParameter("e", email.trim().toLowerCase()).uniqueResult();
        }
    }
}

