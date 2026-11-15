package repository;

import entity.HoaDon;
import entity.KhachHang;
import org.hibernate.Session;

import java.math.BigDecimal;

/**
 * Repository cua bang HoaDon. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Can ham rieng thi viet them vao day.
 */
public class HoaDonRepository extends BaseRepository<HoaDon> {

    public HoaDonRepository() {
        super(HoaDon.class, "maHoaDon");
    }

    // =====================================================================
    // PHAN GIO HANG (M5): gio hang = HoaDon co trangThai GIO_HANG
    // =====================================================================

    // note code nhu nay nhe ban: moi khach toi da 1 gio hang (co unique index UX_HoaDon_gioHang trong SQL)
    public HoaDon timGioHangCuaKhach(Long maKhach) {
        try (Session s = moSession()) {
            return timGioHangCuaKhach(s, maKhach);
        }
    }

    // ban dung chung session voi transaction ben ngoai (GioHangService / DatHangService)
    public HoaDon timGioHangCuaKhach(Session s, Long maKhach) {
        return s.createQuery("from HoaDon h where h.khachHang.maKhachHang = :k and h.trangThai = 'GIO_HANG'", HoaDon.class)
                .setParameter("k", maKhach).uniqueResult();
    }

    // tao gio hang rong cho khach (tien = 0 de khop CHECK CK_HoaDon_tien: tongTien = tamTinh - soTienGiam)
    public HoaDon taoGioHang(Session s, KhachHang khach) {
        HoaDon gio = new HoaDon();
        gio.setKhachHang(khach);
        gio.setTamTinh(BigDecimal.ZERO);
        gio.setSoTienGiam(BigDecimal.ZERO);
        gio.setTongTien(BigDecimal.ZERO);
        gio.setTrangThai("GIO_HANG");
        s.persist(gio);          // IDENTITY nen INSERT ngay -> co maHoaDon de ghi lich su; trung gio thi unique index bao loi o day
        return gio;
    }
}
