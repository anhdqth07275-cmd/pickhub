package repository;

import org.hibernate.Session;
import util.HibernateConfig;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cac truy van thong ke cho Dashboard. Chi tinh doanh thu tu don HOAN_THANH, moc la ngayGiaoThanhCong.
 */
public class ThongKeRepository {

    // Tong doanh thu trong khoang [tu, den)
    public BigDecimal doanhThu(LocalDateTime tu, LocalDateTime den) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            Object kq = s.createQuery("select sum(h.tongTien) from HoaDon h where h.trangThai = 'HOAN_THANH' "
                            + "and h.ngayGiaoThanhCong >= :tu and h.ngayGiaoThanhCong < :den")
                    .setParameter("tu", tu).setParameter("den", den).uniqueResult();
            return kq == null ? BigDecimal.ZERO : new BigDecimal(kq.toString());
        }
    }

    // Doanh thu theo tung ngay tu moc "tu". Tra ve cac dong {"2026-11-01", BigDecimal}
    @SuppressWarnings("unchecked")
    public List<Object[]> doanhThuTheoNgay(LocalDateTime tu) {
        // note code nhu nay nhe ban: CONVERT(char(10), ..., 120) -> chuoi yyyy-MM-dd, nhom theo ngay
        String sql = "SELECT CONVERT(char(10), ngayGiaoThanhCong, 120) AS ngay, SUM(tongTien) AS tien FROM HoaDon "
                + "WHERE trangThai = 'HOAN_THANH' AND ngayGiaoThanhCong >= :tu "
                + "GROUP BY CONVERT(char(10), ngayGiaoThanhCong, 120)";
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createNativeQuery(sql).setParameter("tu", Timestamp.valueOf(tu)).list();
        }
    }

    // Doanh thu theo tung thang. Tra ve cac dong {"2026-11", BigDecimal}
    @SuppressWarnings("unchecked")
    public List<Object[]> doanhThuTheoThang(LocalDateTime tu) {
        String sql = "SELECT CONVERT(char(7), ngayGiaoThanhCong, 120) AS thang, SUM(tongTien) AS tien FROM HoaDon "
                + "WHERE trangThai = 'HOAN_THANH' AND ngayGiaoThanhCong >= :tu "
                + "GROUP BY CONVERT(char(7), ngayGiaoThanhCong, 120)";
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createNativeQuery(sql).setParameter("tu", Timestamp.valueOf(tu)).list();
        }
    }

    // Top san pham ban chay (chi tinh don HOAN_THANH). Tra ve {tenSanPham, tongSoLuong}
    @SuppressWarnings("unchecked")
    public List<Object[]> topBanChay(int soLuong) {
        String sql = "SELECT TOP " + soLuong + " ct.tenSanPham, SUM(ct.soLuong) AS sl FROM HoaDonChiTiet ct "
                + "JOIN HoaDon h ON h.maHoaDon = ct.maHoaDon WHERE h.trangThai = 'HOAN_THANH' "
                + "GROUP BY ct.tenSanPham ORDER BY sl DESC";
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createNativeQuery(sql).list();
        }
    }
    // note code nhu nay nhe ban: tai khoan he thong "Khach le tai quay" khong tinh la khach hang that
    private static final String EMAIL_KHACH_LE = "khachle@pickhub.local";

    // Dem khach hang that (bo khach le tai quay)
    public long demKhachThat() {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            Object kq = s.createQuery("select count(k) from KhachHang k where k.thuDienTu <> :le")
                    .setParameter("le", EMAIL_KHACH_LE).uniqueResult();
            return kq == null ? 0 : ((Number) kq).longValue();
        }
    }

    // Dem khach dang ky moi trong khoang [tu, den)
    public long demKhachMoi(LocalDateTime tu, LocalDateTime den) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            Object kq = s.createQuery("select count(k) from KhachHang k where k.thuDienTu <> :le "
                    + "and k.ngayTao >= :tu and k.ngayTao < :den")
                    .setParameter("le", EMAIL_KHACH_LE)
                    .setParameter("tu", tu).setParameter("den", den).uniqueResult();
            return kq == null ? 0 : ((Number) kq).longValue();
        }
    }

    // Dem don HOAN_THANH trong khoang [tu, den) - dung de tinh gia tri trung binh moi don
    public long demDonHoanThanh(LocalDateTime tu, LocalDateTime den) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            Object kq = s.createQuery("select count(h) from HoaDon h where h.trangThai = 'HOAN_THANH' "
                    + "and h.ngayGiaoThanhCong >= :tu and h.ngayGiaoThanhCong < :den")
                    .setParameter("tu", tu).setParameter("den", den).uniqueResult();
            return kq == null ? 0 : ((Number) kq).longValue();
        }
    }

    // Doanh thu theo danh muc CHA (Vot / Bong / Phu kien / Tui). Tra ve {tenDanhMuc, tongTien}
    @SuppressWarnings("unchecked")
    public List<Object[]> doanhThuTheoDanhMuc(LocalDateTime tu, LocalDateTime den) {
        // note code nhu nay nhe ban: SanPham tro vao danh muc CON (vd Vot Carbon) -> COALESCE(cha, chinh no) de gom len danh muc cha
        // doanh thu = thanhTien tung dong hoa don (chua tru phieu giam gia vi phieu giam tinh tren ca don)
        String sql = "SELECT COALESCE(cha.tenDanhMuc, dm.tenDanhMuc) AS ten, SUM(ct.thanhTien) AS tien "
                + "FROM HoaDonChiTiet ct "
                + "JOIN HoaDon h ON h.maHoaDon = ct.maHoaDon "
                + "JOIN ChiTietSanPham c ON c.maChiTietSanPham = ct.maChiTietSanPham "
                + "JOIN SanPham sp ON sp.maSanPham = c.maSanPham "
                + "JOIN DanhMuc dm ON dm.maDanhMuc = sp.maDanhMuc "
                + "LEFT JOIN DanhMuc cha ON cha.maDanhMuc = dm.maDanhMucCha "
                + "WHERE h.trangThai = 'HOAN_THANH' AND h.ngayGiaoThanhCong >= :tu AND h.ngayGiaoThanhCong < :den "
                + "GROUP BY COALESCE(cha.maDanhMuc, dm.maDanhMuc), COALESCE(cha.tenDanhMuc, dm.tenDanhMuc) "
                + "ORDER BY tien DESC";
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createNativeQuery(sql)
                    .setParameter("tu", Timestamp.valueOf(tu)).setParameter("den", Timestamp.valueOf(den)).list();
        }
    }
}
