package service;

import dto.KetQuaGiaDTO;
import entity.ChiTietChuongTrinhKhuyenMai;
import entity.ChiTietSanPham;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tinh gia khuyen mai (module M4 - phan M5 can dung).
 * Hom nay chi lam ham tinh gia; CRUD chuong trinh khuyen mai van la TODO cua M4.
 */
public class KhuyenMaiService {

    /**
     * Gia sau khuyen mai cua 1 chi tiet san pham khi mua "soLuong" cai.
     * Chi tinh chuong trinh DANG_CHAY, dang trong thoi gian va con du luot (soLuongDaBan + soLuong <= soLuongGioiHan).
     * Co nhieu chuong trinh cung ap thi lay gia THAP NHAT (khong cong don).
     */
    public KetQuaGiaDTO tinhGia(Session s, ChiTietSanPham c, int soLuong) {
        BigDecimal giaGoc = c.getGiaGoc();

        // note code nhu nay nhe ban: lay cac khuyen mai hop le (dung trang thai + dung thoi gian + con luot)
        List<ChiTietChuongTrinhKhuyenMai> ds = s.createQuery(
                "select d from ChiTietChuongTrinhKhuyenMai d, ChuongTrinhKhuyenMai k "
                        + "where d.maChuongTrinhKhuyenMai = k.maChuongTrinhKhuyenMai "
                        + "and d.chiTietSanPham.maChiTietSanPham = :id "
                        + "and k.trangThai = 'DANG_CHAY' and k.ngayBatDau <= :now and k.ngayKetThuc >= :now "
                        + "and (d.soLuongGioiHan is null or d.soLuongDaBan + :sl <= d.soLuongGioiHan)",
                ChiTietChuongTrinhKhuyenMai.class)
                .setParameter("id", c.getMaChiTietSanPham())
                .setParameter("now", LocalDateTime.now())
                .setParameter("sl", soLuong)
                .list();

        BigDecimal giaTotNhat = giaGoc;
        Long maCt = null;
        for (ChiTietChuongTrinhKhuyenMai d : ds) {
            BigDecimal gia = giaSauMotKhuyenMai(giaGoc, d);
            if (gia.compareTo(giaTotNhat) < 0) {       // lay gia thap nhat
                giaTotNhat = gia;
                maCt = d.getMaChiTietChuongTrinhKhuyenMai();
            }
        }
        return new KetQuaGiaDTO(giaGoc, giaTotNhat, maCt);
    }

    // note code nhu nay nhe ban: PHAN_TRAM -> giaGoc * (100 - %) / 100, SO_TIEN -> giaGoc - soTien; lam tron dong, khong am
    // vd doi chieu: 4.890.000 giam 15% = 4.156.500
    private BigDecimal giaSauMotKhuyenMai(BigDecimal giaGoc, ChiTietChuongTrinhKhuyenMai d) {
        BigDecimal gia;
        if ("PHAN_TRAM".equals(d.getLoaiGiam())) {
            gia = giaGoc.multiply(BigDecimal.valueOf(100).subtract(d.getGiaTriGiam())).divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
        } else {
            gia = giaGoc.subtract(d.getGiaTriGiam());
        }
        if (gia.signum() < 0) gia = BigDecimal.ZERO;
        if (gia.compareTo(giaGoc) > 0) gia = giaGoc;
        return gia.setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
