package repository;

import entity.DanhMuc;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.GiaoDich;
import util.HibernateConfig;

import java.util.List;

public class DanhMucRepository extends BaseRepository<DanhMuc> {

    public DanhMucRepository() {
        super(DanhMuc.class, "maDanhMuc");
    }

    /** Lay danh muc goc (danhMucCha is null) dang HOAT_DONG. */
    public List<DanhMuc> layDanhMucGoc() {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createQuery(
                    "select d from DanhMuc d where d.danhMucCha is null and d.trangThai = 'HOAT_DONG' order by d.tenDanhMuc",
                    DanhMuc.class
            ).list();
        }
    }

    /** Lay danh muc con cua maCha dang HOAT_DONG. */
    public List<DanhMuc> layDanhMucCon(Long maCha) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createQuery(
                    "select d from DanhMuc d where d.danhMucCha.maDanhMuc = :maCha and d.trangThai = 'HOAT_DONG' order by d.tenDanhMuc",
                    DanhMuc.class
            ).setParameter("maCha", maCha).list();
        }
    }

    /** Lay tat ca danh muc dang HOAT_DONG (cho dropdown san pham M3). */
    public List<DanhMuc> layDangHoatDong() {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            return s.createQuery(
                    "select d from DanhMuc d where d.trangThai = 'HOAT_DONG' order by d.tenDanhMuc",
                    DanhMuc.class
            ).list();
        }
    }

    /** Dem so san pham thuoc danh muc nay. */
    public long demSanPham(Long maDanhMuc) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            Long count = s.createQuery(
                    "select count(sp) from SanPham sp where sp.danhMuc.maDanhMuc = :maDanhMuc",
                    Long.class
            ).setParameter("maDanhMuc", maDanhMuc).uniqueResult();
            return count == null ? 0L : count;
        }
    }

    /** Kiem tra trung ten danh muc trong cung 1 cha (hoac cung la danh muc goc neu maCha == null). */
    public boolean daTonTai(Long maCha, String ten, Long idBoQua) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            StringBuilder hql = new StringBuilder("select count(d) from DanhMuc d where lower(d.tenDanhMuc) = :ten ");
            if (maCha == null) {
                hql.append("and d.danhMucCha is null ");
            } else {
                hql.append("and d.danhMucCha.maDanhMuc = :maCha ");
            }
            if (idBoQua != null) {
                hql.append("and d.maDanhMuc <> :idBoQua");
            }

            Query<Long> q = s.createQuery(hql.toString(), Long.class);
            q.setParameter("ten", ten.toLowerCase());
            if (maCha != null) q.setParameter("maCha", maCha);
            if (idBoQua != null) q.setParameter("idBoQua", idBoQua);

            Long count = q.uniqueResult();
            return count != null && count > 0;
        }
    }

    /** Dem so danh muc con cua maCha (neu trangThai != null thi loc them theo trangThai). */
    public long demDanhMucCon(Long maCha, String trangThai) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            String hql = "select count(d) from DanhMuc d where d.danhMucCha.maDanhMuc = :maCha"
                    + (trangThai != null ? " and d.trangThai = :trangThai" : "");
            Query<Long> q = s.createQuery(hql, Long.class).setParameter("maCha", maCha);
            if (trangThai != null) {
                q.setParameter("trangThai", trangThai);
            }
            Long count = q.uniqueResult();
            return count == null ? 0L : count;
        }
    }

    // note code nhu nay nhe ban: XOA CUNG 1 danh muc. Chi duoc goi SAU KHI DanhMucService.xoa() da kiem tra
    // danh muc khong con danh muc con va khong co san pham (neu khong se vi pham khoa ngoai trong SQL Server)
    public void xoa(Long maDanhMuc) {
        GiaoDich.chay(s -> {
            s.createQuery("delete from DanhMuc d where d.maDanhMuc = :id").setParameter("id", maDanhMuc).executeUpdate();
            return null;
        });
    }
}
