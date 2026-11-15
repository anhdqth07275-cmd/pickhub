package repository;

import entity.HinhAnhSanPham;
import org.hibernate.Session;

import java.util.List;

/**
 * Repository cua bang HinhAnhSanPham (M3).
 * maChiTietSanPham = NULL nghia la anh chung cua san pham; co gia tri la anh rieng cua 1 bien the.
 */
public class HinhAnhSanPhamRepository extends BaseRepository<HinhAnhSanPham> {

    public HinhAnhSanPhamRepository() {
        super(HinhAnhSanPham.class, "maHinhAnhSanPham");
    }

    public List<HinhAnhSanPham> layTheoSanPham(Long maSanPham) {
        try (Session s = moSession()) {
            return s.createQuery("from HinhAnhSanPham h where h.maSanPham = :id order by h.thuTuSapXep, h.maHinhAnhSanPham", HinhAnhSanPham.class)
                    .setParameter("id", maSanPham).list();
        }
    }

    public HinhAnhSanPham layAnhChinh(Long maSanPham) {
        try (Session s = moSession()) {
            List<HinhAnhSanPham> ds = s.createQuery("from HinhAnhSanPham h where h.maSanPham = :id and h.laAnhChinh = true", HinhAnhSanPham.class)
                    .setParameter("id", maSanPham).setMaxResults(1).list();
            return ds.isEmpty() ? null : ds.get(0);
        }
    }

    /** Duong dan anh chinh cua nhieu san pham cung luc: {maSanPham, duongDanAnh}. */
    public List<Object[]> layAnhChinhNhieuSanPham(List<Long> ids) {
        if (ids.isEmpty()) return new java.util.ArrayList<Object[]>();
        try (Session s = moSession()) {
            return s.createQuery("select h.maSanPham, h.duongDanAnh from HinhAnhSanPham h where h.laAnhChinh = true and h.maSanPham in (:ids)", Object[].class)
                    .setParameterList("ids", ids).list();
        }
    }
}
