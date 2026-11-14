package repository;

import entity.NhanVien;
import org.hibernate.Session;

/**
 * Repository cua bang NhanVien. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class NhanVienRepository extends BaseRepository<NhanVien> {

    public NhanVienRepository() {
        super(NhanVien.class, "maNhanVien");
    }

    // note code nhu nay nhe ban: tim nhan vien theo email (khong phan biet hoa thuong) de dang nhap
    public NhanVien timTheoEmail(String email) {
        try (Session s = moSession()) {
            return s.createQuery("from NhanVien n where lower(n.thuDienTu) = :e", NhanVien.class)
                    .setParameter("e", email.trim().toLowerCase()).uniqueResult();
        }
    }
}
