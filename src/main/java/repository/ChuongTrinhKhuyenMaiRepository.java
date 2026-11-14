package repository;

import entity.ChuongTrinhKhuyenMai;
import util.GiaoDich;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository cua bang ChuongTrinhKhuyenMai (M4). Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class ChuongTrinhKhuyenMaiRepository extends BaseRepository<ChuongTrinhKhuyenMai> {

    public ChuongTrinhKhuyenMaiRepository() {
        super(ChuongTrinhKhuyenMai.class, "maChuongTrinhKhuyenMai");
    }

    // cac chuong trinh dang chay (dung cho dashboard / hien thi)
    public List<ChuongTrinhKhuyenMai> layDangChay() {
        return layCoDieuKien("x.trangThai = 'DANG_CHAY'", "x.ngayKetThuc asc", 100);
    }

    /**
     * Scheduler goi ham nay moi phut: tu doi trang thai theo thoi gian.
     * Tra ve so dong da doi.
     */
    public int capNhatTrangThaiTheoThoiGian(final LocalDateTime bayGio) {
        return GiaoDich.chay(s -> {
            // note code nhu nay nhe ban: HQL update hang loat khong qua @PreUpdate nen phai tu set ngayCapNhat
            // buoc 1: da qua ngayKetThuc -> KET_THUC (ke ca chuong trinh dang TAM_DUNG)
            int ketThuc = s.createQuery("update ChuongTrinhKhuyenMai c set c.trangThai = 'KET_THUC', c.ngayCapNhat = :bayGio "
                            + "where c.trangThai in ('SAP_CHAY','DANG_CHAY','TAM_DUNG') and c.ngayKetThuc < :bayGio")
                    .setParameter("bayGio", bayGio).executeUpdate();

            // buoc 2: den gio bat dau -> DANG_CHAY (TAM_DUNG la admin chu dong dung nen KHONG tu chay lai)
            int batDau = s.createQuery("update ChuongTrinhKhuyenMai c set c.trangThai = 'DANG_CHAY', c.ngayCapNhat = :bayGio "
                            + "where c.trangThai = 'SAP_CHAY' and c.ngayBatDau <= :bayGio and c.ngayKetThuc >= :bayGio")
                    .setParameter("bayGio", bayGio).executeUpdate();
            return ketThuc + batDau;
        });
    }
}
