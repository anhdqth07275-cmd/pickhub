package repository;

import entity.ThuongHieu;

import java.util.List;

/**
 * Repository cua bang ThuongHieu. Da co san: timTheoId, layTatCa, dem, timKiem, luu, capNhat (ke thua BaseRepository).
 */
public class ThuongHieuRepository extends BaseRepository<ThuongHieu> {

    public ThuongHieuRepository() {
        super(ThuongHieu.class, "maThuongHieu");
    }

    // note code nhu nay nhe ban: dropdown thuong hieu cho form san pham (M3) - chi lay HOAT_DONG
    public List<ThuongHieu> layDangHoatDong() {
        return layCoDieuKien("x.trangThai = 'HOAT_DONG'", "x.tenThuongHieu asc", 1000);
    }
}
