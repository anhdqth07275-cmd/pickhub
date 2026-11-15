package repository;

import entity.ChiTietSanPham;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository cua bang ChiTietSanPham = bien the san pham (M3). Bang nay cung la noi QUAN LY TON KHO.
 */
public class ChiTietSanPhamRepository extends BaseRepository<ChiTietSanPham> {

    public ChiTietSanPhamRepository() {
        super(ChiTietSanPham.class, "maChiTietSanPham");
    }

    public List<ChiTietSanPham> layTheoSanPham(Long maSanPham) {
        try (Session s = moSession()) {
            return s.createQuery("from ChiTietSanPham c where c.sanPham.maSanPham = :id order by c.maChiTietSanPham", ChiTietSanPham.class)
                    .setParameter("id", maSanPham).list();
        }
    }

    // note code nhu nay nhe ban: ma hang (SKU) khong duoc trung (khong phan biet hoa thuong) -> dung ham daTonTai cua BaseRepository
    public boolean trungMaHang(String maHang, Long idLoaiTru) {
        return daTonTai("maHang", maHang, idLoaiTru);
    }

    /**
     * Kiem tra to hop thuoc tinh da co trong CUNG san pham chua (vd 2 dong cung Den + 225g + Carbon + 4.25).
     * Cac thuoc tinh co the null nen so sanh bang "is null" khi khong chon.
     */
    public boolean trungToHop(Long maSanPham, Long mau, Long khoiLuong, Long chatLieu, Long chuVi, Long idLoaiTru) {
        StringBuilder hql = new StringBuilder("select count(c) from ChiTietSanPham c where c.sanPham.maSanPham = :sp");
        hql.append(mau == null ? " and c.mauSac is null" : " and c.mauSac.maMauSac = :ms");
        hql.append(khoiLuong == null ? " and c.khoiLuong is null" : " and c.khoiLuong.maKhoiLuong = :kl");
        hql.append(chatLieu == null ? " and c.chatLieu is null" : " and c.chatLieu.maChatLieu = :cl");
        hql.append(chuVi == null ? " and c.chuViTayCam is null" : " and c.chuViTayCam.maChuViTayCam = :cv");
        if (idLoaiTru != null) hql.append(" and c.maChiTietSanPham <> :id");
        try (Session s = moSession()) {
            org.hibernate.query.Query<Long> q = s.createQuery(hql.toString(), Long.class).setParameter("sp", maSanPham);
            if (mau != null) q.setParameter("ms", mau);
            if (khoiLuong != null) q.setParameter("kl", khoiLuong);
            if (chatLieu != null) q.setParameter("cl", chatLieu);
            if (chuVi != null) q.setParameter("cv", chuVi);
            if (idLoaiTru != null) q.setParameter("id", idLoaiTru);
            return q.uniqueResult() > 0;
        }
    }

    /** Bien the da tung nam trong hoa don hoac khuyen mai thi KHONG duoc xoa cung (se loi khoa ngoai). */
    public long demDaDuocSuDung(Long maChiTietSanPham) {
        try (Session s = moSession()) {
            long trongDon = s.createQuery("select count(h) from HoaDonChiTiet h where h.maChiTietSanPham = :id", Long.class)
                    .setParameter("id", maChiTietSanPham).uniqueResult();
            long trongKm = s.createQuery("select count(k) from ChiTietChuongTrinhKhuyenMai k where k.chiTietSanPham.maChiTietSanPham = :id", Long.class)
                    .setParameter("id", maChiTietSanPham).uniqueResult();
            return trongDon + trongKm;
        }
    }

    /** Cac bien the sap het hang (ton <= nguong) va chua ngung ban, it hang nhat len truoc. */
    public List<ChiTietSanPham> laySapHet(int soLuong) {
        return layCoDieuKien("x.soLuongTon <= x.nguongCanhBaoTonKho and x.trangThai <> 'NGUNG_BAN'", "x.soLuongTon asc", soLuong);
    }

    public long demSapHet() {
        return demCoDieuKien("x.soLuongTon <= x.nguongCanhBaoTonKho and x.trangThai <> 'NGUNG_BAN'");
    }
    public boolean truTon(Session s, Long maChiTietSanPham, int soLuong) {
        LocalDateTime bayGio = LocalDateTime.now();
        // note code nhu nay nhe ban: UPDATE ... WHERE soLuongTon >= sl. 0 dong bi anh huong = co nguoi vua mua truoc -> false de caller rollback
        int dong = s.createQuery("update ChiTietSanPham c set c.soLuongTon = c.soLuongTon - :sl, c.ngayCapNhat = :now "
                + "where c.maChiTietSanPham = :id and c.soLuongTon >= :sl and c.trangThai = 'DANG_BAN'")
                .setParameter("sl", soLuong).setParameter("now", bayGio).setParameter("id", maChiTietSanPham).executeUpdate();
        if (dong == 0) return false;

        // ton ve 0 thi tu chuyen HET_HANG
        s.createQuery("update ChiTietSanPham c set c.trangThai = 'HET_HANG', c.ngayCapNhat = :now "
                + "where c.maChiTietSanPham = :id and c.soLuongTon = 0 and c.trangThai = 'DANG_BAN'")
                .setParameter("now", bayGio).setParameter("id", maChiTietSanPham).executeUpdate();
        return true;
    }
}
