package repository;

import dto.BoLoc;
import dto.PhanTrang;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.GiaoDich;
import util.HibernateConfig;

import java.util.List;

/**
 * Repository dung chung: moi repository cua module ke thua lop nay.
 * lop    = class entity (vd ThuongHieu.class)
 * tenId  = ten thuoc tinh khoa chinh trong entity (vd "maThuongHieu")
 */
public class BaseRepository<T> {

    protected final Class<T> lop;
    protected final String tenId;

    public BaseRepository(Class<T> lop, String tenId) {
        this.lop = lop;
        this.tenId = tenId;
    }

    protected Session moSession() {
        return HibernateConfig.getFACTORY().openSession();
    }

    public T timTheoId(Long id) {
        if (id == null) return null;
        try (Session s = moSession()) {
            return s.get(lop, id);
        }
    }

    public List<T> layTatCa() {
        try (Session s = moSession()) {
            return s.createQuery("from " + lop.getSimpleName() + " x order by x." + tenId + " asc", lop).list();
        }
    }

    public long dem() {
        return demCoDieuKien(null);
    }

    // note code nhu nay nhe ban: dem so dong theo dieu kien HQL, vd "x.trangThai = 'CHO_XAC_NHAN'"
    public long demCoDieuKien(String dieuKien) {
        String hql = "select count(x) from " + lop.getSimpleName() + " x";
        if (dieuKien != null && !dieuKien.isEmpty()) hql += " where " + dieuKien;
        try (Session s = moSession()) {
            return (Long) s.createQuery(hql).uniqueResult();
        }
    }

    // lay N dong dau theo dieu kien + sap xep, vd layCoDieuKien("x.soLuongTon <= x.nguongCanhBaoTonKho", "x.soLuongTon asc", 5)
    public List<T> layCoDieuKien(String dieuKien, String sapXep, int soLuong) {
        String hql = "from " + lop.getSimpleName() + " x";
        if (dieuKien != null && !dieuKien.isEmpty()) hql += " where " + dieuKien;
        hql += " order by " + (sapXep != null ? sapXep : "x." + tenId + " desc");
        try (Session s = moSession()) {
            return s.createQuery(hql, lop).setMaxResults(soLuong).list();
        }
    }

    /**
     * Tim kiem + loc trang thai + phan trang (dung cho moi man hinh danh sach).
     */
    public PhanTrang<T> timKiem(BoLoc loc) {
        String ten = lop.getSimpleName();

        // note code nhu nay nhe ban: ghep dieu kien WHERE dong; gia tri nguoi dung luon di qua tham so (:tk, :tt) nen an toan
        StringBuilder dk = new StringBuilder(" where 1 = 1");
        boolean coTuKhoa = loc.getTuKhoa() != null && !loc.getTuKhoa().trim().isEmpty() && loc.getCotTim() != null;
        boolean coTrangThai = loc.getTrangThai() != null && !loc.getTrangThai().trim().isEmpty() && loc.getCotTrangThai() != null;

        if (coTuKhoa) {
            String[] cot = loc.getCotTim().split(",");       // tim tren nhieu cot: "hoTen,thuDienTu"
            dk.append(" and (");
            for (int i = 0; i < cot.length; i++) {
                if (i > 0) dk.append(" or ");
                dk.append("lower(x.").append(cot[i].trim()).append(") like :tk");
            }
            dk.append(")");
        }
        if (coTrangThai) dk.append(" and x.").append(loc.getCotTrangThai()).append(" = :tt");
        if (loc.getDieuKienThem() != null) dk.append(" and ").append(loc.getDieuKienThem());

        String sapXep = loc.getSapXep() != null ? loc.getSapXep() : "x." + tenId + " desc";

        try (Session s = moSession()) {
            Query<Long> demQ = s.createQuery("select count(x) from " + ten + " x" + dk, Long.class);
            Query<T> listQ = s.createQuery("from " + ten + " x" + dk + " order by " + sapXep, lop);
            if (coTuKhoa) {
                String tk = "%" + loc.getTuKhoa().trim().toLowerCase() + "%";
                demQ.setParameter("tk", tk);
                listQ.setParameter("tk", tk);
            }
            if (coTrangThai) {
                demQ.setParameter("tt", loc.getTrangThai().trim());
                listQ.setParameter("tt", loc.getTrangThai().trim());
            }
            long tongSo = demQ.uniqueResult();
            listQ.setFirstResult((loc.getTrang() - 1) * loc.getKichThuoc());   // bo qua cac dong cua trang truoc
            listQ.setMaxResults(loc.getKichThuoc());
            return new PhanTrang<T>(listQ.list(), loc.getTrang(), loc.getKichThuoc(), tongSo);
        }
    }

    // note code nhu nay nhe ban: kiem tra trung, idLoaiTru = id dang sua (de khong tu trung chinh no), them moi thi truyen null
    public boolean daTonTai(String cot, Object giaTri, Long idLoaiTru) {
        // chuoi thi so sanh khong phan biet hoa thuong, so thi so sanh truc tiep
        String veTrai = giaTri instanceof String ? "lower(x." + cot + ")" : "x." + cot;
        String hql = "select count(x) from " + lop.getSimpleName() + " x where " + veTrai + " = :v";
        if (idLoaiTru != null) hql += " and x." + tenId + " <> :id";
        try (Session s = moSession()) {
            Query<Long> q = s.createQuery(hql, Long.class);
            q.setParameter("v", giaTri instanceof String ? ((String) giaTri).toLowerCase() : giaTri);
            if (idLoaiTru != null) q.setParameter("id", idLoaiTru);
            return q.uniqueResult() > 0;
        }
    }

    public void luu(final T doiTuong) {
        GiaoDich.chay(s -> { s.persist(doiTuong); return null; });
    }

    public void capNhat(final T doiTuong) {
        GiaoDich.chay(s -> { s.merge(doiTuong); return null; });
    }
    // note code nhu nay nhe ban: lay cac dong HOAT_DONG de do vao dropdown (ap dung cho bang co cot trangThai: ThuongHieu, DanhMuc, MauSac, KhoiLuong, ChatLieu, ChuViTayCam)
    public List<T> layDangHoatDong() {
        try (Session s = moSession()) {
            return s.createQuery("from " + lop.getSimpleName() + " x where x.trangThai = 'HOAT_DONG' order by x." + tenId + " asc", lop).list();
        }
    }
}
