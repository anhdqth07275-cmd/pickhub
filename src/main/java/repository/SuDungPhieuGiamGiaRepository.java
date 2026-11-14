package repository;

import entity.SuDungPhieuGiamGia;
import org.hibernate.Session;

/**
 * Repository cua bang SuDungPhieuGiamGia (M4). Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class SuDungPhieuGiamGiaRepository extends BaseRepository<SuDungPhieuGiamGia> {

    public SuDungPhieuGiamGiaRepository() {
        super(SuDungPhieuGiamGia.class, "maSuDungPhieuGiamGia");
    }

    // note code nhu nay nhe ban: chi dem luot DA_SU_DUNG (luot da hoan khi huy don thi khach duoc dung lai)
    public long demLuotDung(Long maPhieu, Long maKhachHang) {
        try (Session s = moSession()) {
            return demLuotDung(s, maPhieu, maKhachHang);
        }
    }

    // ban dung chung session voi transaction ben ngoai (SuDungPhieuService)
    public long demLuotDung(Session s, Long maPhieu, Long maKhachHang) {
        return s.createQuery("select count(x) from SuDungPhieuGiamGia x where x.phieuGiamGia.maPhieuGiamGia = :p "
                        + "and x.khachHang.maKhachHang = :k and x.trangThai = 'DA_SU_DUNG'", Long.class)
                .setParameter("p", maPhieu).setParameter("k", maKhachHang).uniqueResult();
    }

    // 1 hoa don toi da 1 luot dung phieu (UNIQUE maHoaDon)
    public SuDungPhieuGiamGia timTheoHoaDon(Session s, Long maHoaDon) {
        return s.createQuery("from SuDungPhieuGiamGia x where x.maHoaDon = :hd", SuDungPhieuGiamGia.class)
                .setParameter("hd", maHoaDon).uniqueResult();
    }
}
