package repository;

import entity.PhieuGiamGia;
import org.hibernate.Session;
import util.GiaoDich;

import java.time.LocalDateTime;

/**
 * Repository cua bang PhieuGiamGia (M4). Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat, daTonTai (ke thua BaseRepository).
 */
public class PhieuGiamGiaRepository extends BaseRepository<PhieuGiamGia> {

    public PhieuGiamGiaRepository() {
        super(PhieuGiamGia.class, "maPhieuGiamGia");
    }

    // tim theo ma khach go (khong phan biet hoa thuong), khong co thi tra ve null
    public PhieuGiamGia timTheoMa(String maGiamGia) {
        if (maGiamGia == null) return null;
        try (Session s = moSession()) {
            return s.createQuery("from PhieuGiamGia p where upper(p.maGiamGia) = :ma", PhieuGiamGia.class)
                    .setParameter("ma", maGiamGia.trim().toUpperCase()).uniqueResult();
        }
    }

    // note code nhu nay nhe ban: UPDATE co dieu kien chong vuot gioi han khi nhieu nguoi dat cung luc; false = da het luot
    public boolean tangSoLanDaSuDung(Session s, Long maPhieu) {
        int dong = s.createQuery("update PhieuGiamGia p set p.soLanDaSuDung = p.soLanDaSuDung + 1, p.ngayCapNhat = :bayGio "
                        + "where p.maPhieuGiamGia = :id and (p.gioiHanSuDung is null or p.soLanDaSuDung < p.gioiHanSuDung)")
                .setParameter("bayGio", LocalDateTime.now()).setParameter("id", maPhieu).executeUpdate();
        return dong > 0;
    }

    // hoan 1 luot khi huy don (khong cho xuong am)
    public boolean giamSoLanDaSuDung(Session s, Long maPhieu) {
        int dong = s.createQuery("update PhieuGiamGia p set p.soLanDaSuDung = p.soLanDaSuDung - 1, p.ngayCapNhat = :bayGio "
                        + "where p.maPhieuGiamGia = :id and p.soLanDaSuDung > 0")
                .setParameter("bayGio", LocalDateTime.now()).setParameter("id", maPhieu).executeUpdate();
        return dong > 0;
    }

    /** Scheduler goi moi phut: SAP_CHAY -> DANG_CHAY -> HET_HAN theo thoi gian. Tra ve so dong da doi. */
    public int capNhatTrangThaiTheoThoiGian(final LocalDateTime bayGio) {
        return GiaoDich.chay(s -> {
            int hetHan = s.createQuery("update PhieuGiamGia p set p.trangThai = 'HET_HAN', p.ngayCapNhat = :bayGio "
                            + "where p.trangThai in ('SAP_CHAY','DANG_CHAY','TAM_DUNG') and p.ngayKetThuc < :bayGio")
                    .setParameter("bayGio", bayGio).executeUpdate();
            // note code nhu nay nhe ban: TAM_DUNG la admin chu dong dung nen KHONG tu chay lai
            int batDau = s.createQuery("update PhieuGiamGia p set p.trangThai = 'DANG_CHAY', p.ngayCapNhat = :bayGio "
                            + "where p.trangThai = 'SAP_CHAY' and p.ngayBatDau <= :bayGio and p.ngayKetThuc >= :bayGio")
                    .setParameter("bayGio", bayGio).executeUpdate();
            return hetHan + batDau;
        });
    }
}
