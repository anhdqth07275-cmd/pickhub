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

    // =====================================================================
    // PHAN GIO HANG (M5) - cac ham nhan Session de chay chung 1 transaction voi GioHangService / DatHangService
    // =====================================================================

    public List<HoaDonChiTiet> timTheoHoaDon(Session s, Long maHoaDon) {
        return s.createQuery("from HoaDonChiTiet c where c.maHoaDon = :id order by c.maHoaDonChiTiet", HoaDonChiTiet.class)
                .setParameter("id", maHoaDon).list();
    }

    // note code nhu nay nhe ban: UNIQUE (maHoaDon, maChiTietSanPham) nen 1 san pham chi co toi da 1 dong trong gio
    public HoaDonChiTiet timDong(Session s, Long maHoaDon, Long maChiTietSanPham) {
        return s.createQuery("from HoaDonChiTiet c where c.maHoaDon = :h and c.maChiTietSanPham = :sp", HoaDonChiTiet.class)
                .setParameter("h", maHoaDon).setParameter("sp", maChiTietSanPham).uniqueResult();
    }

    public void themDong(Session s, HoaDonChiTiet dong) {
        s.persist(dong);
    }

    public void xoaDong(Session s, HoaDonChiTiet dong) {
        s.remove(dong);
    }

    /** Xoa het cac dong cua 1 gio. Tra ve so dong da xoa. */
    public int xoaTatCa(Session s, Long maHoaDon) {
        return s.createQuery("delete from HoaDonChiTiet c where c.maHoaDon = :h").setParameter("h", maHoaDon).executeUpdate();
    }

    // tong so luong san pham trong gio cua 1 khach (hien tren icon gio o header)
    public int demSoLuongTrongGio(Long maKhach) {
        try (Session s = moSession()) {
            Long tong = s.createQuery("select sum(d.soLuong) from HoaDonChiTiet d, HoaDon h where d.maHoaDon = h.maHoaDon "
                    + "and h.khachHang.maKhachHang = :k and h.trangThai = 'GIO_HANG'", Long.class)
                    .setParameter("k", maKhach).uniqueResult();
            return tong == null ? 0 : tong.intValue();
        }
    }
}
