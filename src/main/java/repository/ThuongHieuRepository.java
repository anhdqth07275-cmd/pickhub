package repository;

import entity.ThuongHieu;
import org.hibernate.Session;

import java.util.List;

/**
 * Repository cua bang ThuongHieu (M2).
 */
public class ThuongHieuRepository extends BaseRepository<ThuongHieu> {

    public ThuongHieuRepository() {
        super(ThuongHieu.class, "maThuongHieu");
    }

    public List<ThuongHieu> layDangHoatDong() {
        try (Session s = moSession()) {
            return s.createQuery("from ThuongHieu t where t.trangThai = 'HOAT_DONG' order by t.tenThuongHieu", ThuongHieu.class).list();
        }
    }

    public long demSanPhamDangHienThi(Long maThuongHieu) {
        try (Session s = moSession()) {
            return s.createQuery("select count(p) from SanPham p where p.thuongHieu.maThuongHieu = :id and p.dangHienThi = true", Long.class)
                    .setParameter("id", maThuongHieu).uniqueResult();
        }
    }
}
