package repository;

import dto.SanPhamLocDTO;
import entity.SanPham;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository cua bang SanPham (M3).
 * Da co san tu BaseRepository: timTheoId, layTatCa, dem, timKiem, daTonTai, luu, capNhat.
 */
public class SanPhamRepository extends BaseRepository<SanPham> {

    public SanPhamRepository() {
        super(SanPham.class, "maSanPham");
    }

    /**
     * Tim ID san pham theo nhieu dieu kien ket hop (da loc, CHUA phan trang).
     * note code nhu nay nhe ban: moi dieu kien chi duoc ghep vao WHERE khi nguoi dung co nhap;
     * gia tri nguoi dung luon di qua tham so (:tk, :dm...) nen khong bi HQL injection.
     * Loc theo gia / mau / khoi luong / ton kho nam o bang ChiTietSanPham nen dung EXISTS.
     */
    public List<Long> timIdTheoBoLoc(SanPhamLocDTO loc) {
        StringBuilder hql = new StringBuilder("select s.maSanPham from SanPham s where 1 = 1");
        List<Object[]> thamSo = new ArrayList<Object[]>();   // moi phan tu = {ten, gia tri}

        if (coChu(loc.getTuKhoa())) {
            // tim theo ten san pham HOAC ma hang (SKU) cua bat ky bien the nao
            hql.append(" and (lower(s.tenSanPham) like :tk or exists (select 1 from ChiTietSanPham c where c.sanPham = s and lower(c.maHang) like :tk))");
            thamSo.add(new Object[]{"tk", "%" + loc.getTuKhoa().trim().toLowerCase() + "%"});
        }
        if (loc.getMaDanhMuc() != null) {
            // note code nhu nay nhe ban: chon danh muc CHA thi lay luon san pham cua cac danh muc CON
            hql.append(" and (s.danhMuc.maDanhMuc = :dm or s.danhMuc.danhMucCha.maDanhMuc = :dm)");
            thamSo.add(new Object[]{"dm", loc.getMaDanhMuc()});
        }
        if (loc.getMaThuongHieu() != null) {
            hql.append(" and s.thuongHieu.maThuongHieu = :th");
            thamSo.add(new Object[]{"th", loc.getMaThuongHieu()});
        }
        if (loc.getDangHienThi() != null) {
            hql.append(" and s.dangHienThi = :ht");
            thamSo.add(new Object[]{"ht", loc.getDangHienThi()});
        }
        if (loc.getMaMauSac() != null) {
            hql.append(" and exists (select 1 from ChiTietSanPham c where c.sanPham = s and c.mauSac.maMauSac = :ms)");
            thamSo.add(new Object[]{"ms", loc.getMaMauSac()});
        }
        if (loc.getMaKhoiLuong() != null) {
            hql.append(" and exists (select 1 from ChiTietSanPham c where c.sanPham = s and c.khoiLuong.maKhoiLuong = :kl)");
            thamSo.add(new Object[]{"kl", loc.getMaKhoiLuong()});
        }
        if (loc.getGiaTu() != null) {
            hql.append(" and exists (select 1 from ChiTietSanPham c where c.sanPham = s and c.giaGoc >= :giaTu)");
            thamSo.add(new Object[]{"giaTu", loc.getGiaTu()});
        }
        if (loc.getGiaDen() != null) {
            hql.append(" and exists (select 1 from ChiTietSanPham c where c.sanPham = s and c.giaGoc <= :giaDen)");
            thamSo.add(new Object[]{"giaDen", loc.getGiaDen()});
        }
        // loc tinh trang ton: cac chuoi nay do CODE quy dinh (khong lay nguyen van tu nguoi dung)
        if ("HET".equals(loc.getTinhTrangTon())) {
            hql.append(" and exists (select 1 from ChiTietSanPham c where c.sanPham = s and c.soLuongTon = 0)");
        } else if ("SAP_HET".equals(loc.getTinhTrangTon())) {
            hql.append(" and exists (select 1 from ChiTietSanPham c where c.sanPham = s and c.soLuongTon > 0 and c.soLuongTon <= c.nguongCanhBaoTonKho)");
        } else if ("CON".equals(loc.getTinhTrangTon())) {
            hql.append(" and exists (select 1 from ChiTietSanPham c where c.sanPham = s and c.soLuongTon > c.nguongCanhBaoTonKho)");
        }

        // sap xep theo ten / moi nhat o day; sap xep theo GIA lam trong Service (gia nam o bang khac)
        hql.append("ten".equals(loc.getSapXep()) ? " order by s.tenSanPham asc" : " order by s.maSanPham desc");

        try (Session session = moSession()) {
            Query<Long> q = session.createQuery(hql.toString(), Long.class);
            for (Object[] tp : thamSo) q.setParameter((String) tp[0], tp[1]);
            return q.list();
        }
    }

    /** Lay san pham theo danh sach ID (khong dam bao thu tu). */
    public List<SanPham> timTheoDanhSachId(List<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<SanPham>();
        try (Session s = moSession()) {
            return s.createQuery("from SanPham x where x.maSanPham in (:ids)", SanPham.class)
                    .setParameterList("ids", ids).list();
        }
    }

    /** Tong hop theo san pham: {maSanPham, giaMin, giaMax, tongTon, soBienThe}. */
    public List<Object[]> tongHopBienThe(List<Long> ids) {
        if (ids.isEmpty()) return new ArrayList<Object[]>();
        try (Session s = moSession()) {
            return s.createQuery("select c.sanPham.maSanPham, min(c.giaGoc), max(c.giaGoc), sum(c.soLuongTon), count(c) "
                    + "from ChiTietSanPham c where c.sanPham.maSanPham in (:ids) group by c.sanPham.maSanPham", Object[].class)
                    .setParameterList("ids", ids).list();
        }
    }

    public long demBienThe(Long maSanPham) {
        try (Session s = moSession()) {
            return s.createQuery("select count(c) from ChiTietSanPham c where c.sanPham.maSanPham = :id", Long.class)
                    .setParameter("id", maSanPham).uniqueResult();
        }
    }

    private boolean coChu(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
