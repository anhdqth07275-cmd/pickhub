package repository;

import entity.ChiTietChuongTrinhKhuyenMai;
import org.hibernate.Session;
import util.GiaoDich;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository cua bang ChiTietChuongTrinhKhuyenMai (M4). Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class ChiTietChuongTrinhKhuyenMaiRepository extends BaseRepository<ChiTietChuongTrinhKhuyenMai> {

    public ChiTietChuongTrinhKhuyenMaiRepository() {
        super(ChiTietChuongTrinhKhuyenMai.class, "maChiTietChuongTrinhKhuyenMai");
    }

    // cac chi tiet san pham duoc giam trong 1 chuong trinh khuyen mai
    public List<ChiTietChuongTrinhKhuyenMai> timTheoChuongTrinh(Long maChuongTrinh) {
        try (Session s = moSession()) {
            return s.createQuery("from ChiTietChuongTrinhKhuyenMai c where c.maChuongTrinhKhuyenMai = :id order by c.maChiTietChuongTrinhKhuyenMai",
                    ChiTietChuongTrinhKhuyenMai.class).setParameter("id", maChuongTrinh).list();
        }
    }

    // note code nhu nay nhe ban: kiem tra 1 bien the da co trong chuong trinh chua (UNIQUE chuong trinh + bien the), idLoaiTru = dong dang sua
    public boolean daTonTaiBienThe(Long maChuongTrinh, Long maChiTietSanPham, Long idLoaiTru) {
        String hql = "select count(c) from ChiTietChuongTrinhKhuyenMai c where c.maChuongTrinhKhuyenMai = :ct "
                + "and c.chiTietSanPham.maChiTietSanPham = :sp";
        if (idLoaiTru != null) hql += " and c.maChiTietChuongTrinhKhuyenMai <> :id";
        try (Session s = moSession()) {
            org.hibernate.query.Query<Long> q = s.createQuery(hql, Long.class)
                    .setParameter("ct", maChuongTrinh).setParameter("sp", maChiTietSanPham);
            if (idLoaiTru != null) q.setParameter("id", idLoaiTru);
            return q.uniqueResult() > 0;
        }
    }

    /**
     * Cac muc giam DANG ap dung cho 1 bien the tai thoi diem "bayGio":
     * chuong trinh DANG_CHAY + trong thoi gian + con luot (soLuongDaBan < soLuongGioiHan hoac khong gioi han).
     */
    public List<ChiTietChuongTrinhKhuyenMai> timKhuyenMaiDangChayCuaBienThe(Long maChiTietSanPham, LocalDateTime bayGio) {
        // note code nhu nay nhe ban: entity chi luu maChuongTrinhKhuyenMai (khong co @ManyToOne) nen noi 2 bang bang dieu kien WHERE
        String hql = "select c from ChiTietChuongTrinhKhuyenMai c, ChuongTrinhKhuyenMai k "
                + "where k.maChuongTrinhKhuyenMai = c.maChuongTrinhKhuyenMai "
                + "and c.chiTietSanPham.maChiTietSanPham = :sp "
                + "and k.trangThai = 'DANG_CHAY' and k.ngayBatDau <= :bayGio and k.ngayKetThuc >= :bayGio "
                + "and (c.soLuongGioiHan is null or c.soLuongDaBan < c.soLuongGioiHan)";
        try (Session s = moSession()) {
            return s.createQuery(hql, ChiTietChuongTrinhKhuyenMai.class)
                    .setParameter("sp", maChiTietSanPham).setParameter("bayGio", bayGio).list();
        }
    }

    /**
     * Tang soLuongDaBan khi dat hang (M6 goi trong transaction cua M6, nen nhan Session tu ben ngoai).
     * Tra ve false neu da het luot.
     */
    public boolean tangSoLuongDaBan(Session s, Long maChiTiet, int soLuong) {
        // note code nhu nay nhe ban: UPDATE co dieu kien -> 2 khach mua cung luc thi chi ai con luot moi thanh cong, khong bao gio vuot gioi han
        int dong = s.createQuery("update ChiTietChuongTrinhKhuyenMai c set c.soLuongDaBan = c.soLuongDaBan + :sl, c.ngayCapNhat = :bayGio "
                        + "where c.maChiTietChuongTrinhKhuyenMai = :id "
                        + "and (c.soLuongGioiHan is null or c.soLuongDaBan + :sl <= c.soLuongGioiHan)")
                .setParameter("sl", soLuong).setParameter("bayGio", LocalDateTime.now())
                .setParameter("id", maChiTiet).executeUpdate();
        return dong > 0;
    }

    // hoan lai luot khuyen mai khi huy don (khong cho xuong am)
    public boolean hoanSoLuongDaBan(Session s, Long maChiTiet, int soLuong) {
        int dong = s.createQuery("update ChiTietChuongTrinhKhuyenMai c set c.soLuongDaBan = c.soLuongDaBan - :sl, c.ngayCapNhat = :bayGio "
                        + "where c.maChiTietChuongTrinhKhuyenMai = :id and c.soLuongDaBan >= :sl")
                .setParameter("sl", soLuong).setParameter("bayGio", LocalDateTime.now())
                .setParameter("id", maChiTiet).executeUpdate();
        return dong > 0;
    }

    // so dong hoa don chi tiet da dung muc giam nay (FK HoaDonChiTiet -> ChiTietChuongTrinhKhuyenMai)
    public long demHoaDonDung(Long maChiTiet) {
        try (Session s = moSession()) {
            return s.createQuery("select count(h) from HoaDonChiTiet h where h.maChiTietChuongTrinhKhuyenMai = :id", Long.class)
                    .setParameter("id", maChiTiet).uniqueResult();
        }
    }

    public void xoa(final Long maChiTiet) {
        GiaoDich.chay(s -> {
            ChiTietChuongTrinhKhuyenMai c = s.get(ChiTietChuongTrinhKhuyenMai.class, maChiTiet);
            if (c != null) s.remove(c);
            return null;
        });
    }
}
