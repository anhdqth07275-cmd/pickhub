package repository;

import entity.ChiTietSanPham;
import org.hibernate.Session;

import java.time.LocalDateTime;
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

    // =====================================================================
    // TON KHO - M5 (dat hang) / M6 (huy don) goi, theo bang phan chia M3 -> M5 "tru ton kho khi dat hang"
    // =====================================================================

    /**
     * Tru ton kho khi dat hang (chay trong transaction cua M5, nen nhan Session tu ben ngoai).
     * Tra ve false neu khong du hang (hoac bien the khong con DANG_BAN).
     */
    public boolean truTon(Session s, Long maChiTietSanPham, int soLuong) {
        LocalDateTime bayGio = LocalDateTime.now();
        // note code nhu nay nhe ban: UPDATE ... WHERE soLuongTon >= sl. 0 dong bi anh huong = co nguoi vua mua truoc -> false de caller rollback
        int dong = s.createQuery("update ChiTietSanPham c set c.soLuongTon = c.soLuongTon - :sl, c.ngayCapNhat = :now "
                + "where c.maChiTietSanPham = :id and c.soLuongTon >= :sl and c.trangThai = 'DANG_BAN'")
                .setParameter("sl", soLuong).setParameter("now", bayGio).setParameter("id", maChiTietSanPham).executeUpdate();
        if (dong == 0) return false;

        // ton ve 0 thi tu chuyen HET_HANG
        s.createQuery("update ChiTietSanPham c set c.trangThai = 'HET_HANG', c.ngayCapNhat = :now "
                + "where c.maChiTietSanPham = :id and c.soLuongTon = 0 and c.trangThai = 'DANG_BAN'")
                .setParameter("now", bayGio).setParameter("id", maChiTietSanPham).executeUpdate();
        return true;
    }

    /** Hoan ton kho khi huy don (M6 / M7 goi). Bien the dang HET_HANG ma co hang tro lai thi chuyen ve DANG_BAN. */
    public void congTon(Session s, Long maChiTietSanPham, int soLuong) {
        LocalDateTime bayGio = LocalDateTime.now();
        s.createQuery("update ChiTietSanPham c set c.soLuongTon = c.soLuongTon + :sl, c.ngayCapNhat = :now where c.maChiTietSanPham = :id")
                .setParameter("sl", soLuong).setParameter("now", bayGio).setParameter("id", maChiTietSanPham).executeUpdate();
        s.createQuery("update ChiTietSanPham c set c.trangThai = 'DANG_BAN', c.ngayCapNhat = :now "
                + "where c.maChiTietSanPham = :id and c.trangThai = 'HET_HANG' and c.soLuongTon > 0")
                .setParameter("now", bayGio).setParameter("id", maChiTietSanPham).executeUpdate();
    }
}
