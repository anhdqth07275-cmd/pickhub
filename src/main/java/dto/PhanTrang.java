package dto;

import java.util.List;

/**
 * Ket qua phan trang: danh sach cua trang hien tai + thong tin trang.
 */
public class PhanTrang<T> {
    private final List<T> danhSach;
    private final int trangHienTai;
    private final int kichThuoc;
    private final long tongSo;

    public PhanTrang(List<T> danhSach, int trangHienTai, int kichThuoc, long tongSo) {
        this.danhSach = danhSach;
        this.trangHienTai = trangHienTai;
        this.kichThuoc = kichThuoc;
        this.tongSo = tongSo;
    }

    public List<T> getDanhSach() { return danhSach; }
    public int getTrangHienTai() { return trangHienTai; }
    public long getTongSo() { return tongSo; }

    // note code nhu nay nhe ban: tong trang = lam tron len (tongSo / kichThuoc)
    public int getTongTrang() {
        return (int) Math.ceil((double) tongSo / kichThuoc);
    }
}
