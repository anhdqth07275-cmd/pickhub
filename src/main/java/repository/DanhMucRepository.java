package repository;

import entity.DanhMuc;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Repository cua bang DanhMuc. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 * Ham rieng cua danh muc cha - con viet o duoi.
 */
public class DanhMucRepository extends BaseRepository<DanhMuc> {

    public DanhMucRepository() {
        super(DanhMuc.class, "maDanhMuc");
    }

    /** Danh muc GOC (khong co cha) dang hoat dong - dung cho dropdown "danh muc cha" va menu. */
    public List<DanhMuc> layDanhMucGoc() {
        return layCoDieuKien("x.danhMucCha is null and x.trangThai = 'HOAT_DONG'", "x.tenDanhMuc asc", 1000);
    }

    /** Danh muc CON dang hoat dong cua 1 danh muc cha. */
    public List<DanhMuc> layDanhMucCon(Long maCha) {
        try (Session s = moSession()) {
            return s.createQuery("from DanhMuc x where x.danhMucCha.maDanhMuc = :cha and x.trangThai = 'HOAT_DONG' order by x.tenDanhMuc asc", DanhMuc.class)
                    .setParameter("cha", maCha)
                    .list();
        }
    }

    /** Tat ca danh muc dang hoat dong (dropdown chon danh muc cho san pham o M3). */
    public List<DanhMuc> layDangHoatDong() {
        return layCoDieuKien("x.trangThai = 'HOAT_DONG'", "x.tenDanhMuc asc", 1000);
    }

    // note code nhu nay nhe ban: dem con theo trang thai (null = dem tat ca). Dung de chan "da co con thi khong duoc lam con" va "con dang hoat dong thi khong duoc ngung cha"
    public long demDanhMucCon(Long maCha, String trangThai) {
        String hql = "select count(x) from DanhMuc x where x.danhMucCha.maDanhMuc = :cha";
        if (trangThai != null) hql += " and x.trangThai = :tt";
        try (Session s = moSession()) {
            Query<Long> q = s.createQuery(hql, Long.class).setParameter("cha", maCha);
            if (trangThai != null) q.setParameter("tt", trangThai);
            return q.uniqueResult();
        }
    }

    /** So san pham dang thuoc danh muc nay. */
    public long demSanPham(Long maDanhMuc) {
        try (Session s = moSession()) {
            return s.createQuery("select count(sp) from SanPham sp where sp.danhMuc.maDanhMuc = :ma", Long.class)
                    .setParameter("ma", maDanhMuc)
                    .uniqueResult();
        }
    }

    // note code nhu nay nhe ban: kiem tra trung CAP (cha, ten) giong UNIQUE(maDanhMucCha, tenDanhMuc) trong SQL.
    // maCha = null nghia la danh muc goc -> phai viet "is null" vi HQL khong so sanh "= null" duoc
    public boolean daTonTai(Long maCha, String ten, Long idLoaiTru) {
        String hql = "select count(x) from DanhMuc x where lower(x.tenDanhMuc) = :ten";
        hql += (maCha == null) ? " and x.danhMucCha is null" : " and x.danhMucCha.maDanhMuc = :cha";
        if (idLoaiTru != null) hql += " and x.maDanhMuc <> :id";
        try (Session s = moSession()) {
            Query<Long> q = s.createQuery(hql, Long.class).setParameter("ten", ten.toLowerCase());
            if (maCha != null) q.setParameter("cha", maCha);
            if (idLoaiTru != null) q.setParameter("id", idLoaiTru);
            return q.uniqueResult() > 0;
        }
    }
}
