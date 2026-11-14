package repository;

import entity.DanhMuc;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class DanhMucRepository extends BaseRepository<DanhMuc> {

    public DanhMucRepository() {
        super(DanhMuc.class, "maDanhMuc");
    }

    // note code nhu nay nhe ban: danh muc GOC (khong co cha), chi lay HOAT_DONG de dung cho menu / bo loc
    public List<DanhMuc> layDanhMucGoc() {
        try (Session s = moSession()) {
            return s.createQuery("from DanhMuc d where d.danhMucCha is null and d.trangThai = 'HOAT_DONG' order by d.maDanhMuc", DanhMuc.class).list();
        }
    }

    // danh muc CON HOAT_DONG cua 1 danh muc cha
    public List<DanhMuc> layDanhMucCon(Long maCha) {
        try (Session s = moSession()) {
            return s.createQuery("from DanhMuc d where d.danhMucCha.maDanhMuc = :cha and d.trangThai = 'HOAT_DONG' order by d.maDanhMuc", DanhMuc.class)
                    .setParameter("cha", maCha).list();
        }
    }

    // note code nhu nay nhe ban: kiem tra trung (cha, ten) theo UNIQUE UQ_DanhMuc_cha_ten.
    // maCha = null -> danh muc goc; idLoaiTru = id dang sua (de khong tu trung chinh no), them moi thi truyen null
    public boolean daTonTai(Long maCha, String ten, Long idLoaiTru) {
        String hql = "select count(d) from DanhMuc d where lower(d.tenDanhMuc) = :ten and "
                + (maCha == null ? "d.danhMucCha is null" : "d.danhMucCha.maDanhMuc = :cha");
        if (idLoaiTru != null) hql += " and d.maDanhMuc <> :id";
        try (Session s = moSession()) {
            Query<Long> q = s.createQuery(hql, Long.class).setParameter("ten", ten.trim().toLowerCase());
            if (maCha != null) q.setParameter("cha", maCha);
            if (idLoaiTru != null) q.setParameter("id", idLoaiTru);
            return q.uniqueResult() > 0;
        }
    }

    // dem danh muc con cua 1 cha; trangThai = null -> dem tat ca, co gia tri (vd "HOAT_DONG") -> chi dem theo trang thai do
    public long demDanhMucCon(Long maCha, String trangThai) {
        String hql = "select count(d) from DanhMuc d where d.danhMucCha.maDanhMuc = :cha";
        if (trangThai != null) hql += " and d.trangThai = :tt";
        try (Session s = moSession()) {
            Query<Long> q = s.createQuery(hql, Long.class).setParameter("cha", maCha);
            if (trangThai != null) q.setParameter("tt", trangThai);
            return q.uniqueResult();
        }
    }

    // so san pham dang thuoc danh muc (quyet dinh co duoc ngung hay khong)
    public long demSanPham(Long maDanhMuc) {
        try (Session s = moSession()) {
            return s.createQuery("select count(p) from SanPham p where p.danhMuc.maDanhMuc = :id", Long.class)
                    .setParameter("id", maDanhMuc).uniqueResult();
        }
    }
}