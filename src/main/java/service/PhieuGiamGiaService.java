package service;

import dto.KetQuaPhieuDTO;
import entity.PhieuGiamGia;
import org.hibernate.Session;
import util.DinhDangUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Kiem tra va tinh tien giam cua phieu giam gia (module M4 - phan M5 can dung).
 */
public class PhieuGiamGiaService {

    /** Tim phieu theo ma (khong phan biet hoa thuong) roi kiem tra. */
    public KetQuaPhieuDTO kiemTraVaTinhGiam(Session s, String ma, Long maKhach, BigDecimal tamTinh) {
        if (ma == null || ma.trim().isEmpty()) return KetQuaPhieuDTO.tuChoi("Vui lòng nhập mã giảm giá");
        PhieuGiamGia p = s.createQuery("from PhieuGiamGia p where upper(p.maGiamGia) = :m", PhieuGiamGia.class)
                .setParameter("m", ma.trim().toUpperCase()).uniqueResult();
        if (p == null) return KetQuaPhieuDTO.tuChoi("Mã giảm giá không tồn tại");
        return kiemTra(s, p, maKhach, tamTinh);
    }

    public KetQuaPhieuDTO kiemTra(Session s, PhieuGiamGia p, Long maKhach, BigDecimal tamTinh) {
        LocalDateTime now = LocalDateTime.now();

        // note code nhu nay nhe ban: kiem tra lan luot cac dieu kien, sai dieu kien nao thi tra ly do do
        if (!"DANG_CHAY".equals(p.getTrangThai())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá hiện không áp dụng được");
        if (now.isBefore(p.getNgayBatDau())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá chưa đến thời gian áp dụng");
        if (now.isAfter(p.getNgayKetThuc())) return KetQuaPhieuDTO.tuChoi("Mã giảm giá đã hết hạn");
        if (p.getGioiHanSuDung() != null && p.getSoLanDaSuDung() >= p.getGioiHanSuDung()) {
            return KetQuaPhieuDTO.tuChoi("Mã giảm giá đã hết lượt sử dụng");
        }

        // moi khach duoc dung toi da gioiHanMoiNguoi lan (chi dem luot DA_SU_DUNG, luot da hoan thi khong tinh)
        Long daDung = s.createQuery("select count(u) from SuDungPhieuGiamGia u where u.phieuGiamGia.maPhieuGiamGia = :p "
                + "and u.khachHang.maKhachHang = :k and u.trangThai = 'DA_SU_DUNG'", Long.class)
                .setParameter("p", p.getMaPhieuGiamGia()).setParameter("k", maKhach).uniqueResult();
        if (daDung >= p.getGioiHanMoiNguoi()) return KetQuaPhieuDTO.tuChoi("Bạn đã dùng hết số lần cho phép của mã này");

        if (tamTinh.compareTo(p.getGiaTriDonToiThieu()) < 0) {
            return KetQuaPhieuDTO.tuChoi("Đơn hàng cần tối thiểu " + DinhDangUtil.tien(p.getGiaTriDonToiThieu()) + " để dùng mã này");
        }

        // note code nhu nay nhe ban: PHAN_TRAM bi chan boi giamToiDa; tien giam khong bao gio vuot tamTinh (CHECK CK_HoaDon_tien)
        // vd doi chieu: don 4.926.600 + DOUBLE11 (11%, toi da 600.000) = 541.926
        BigDecimal giam;
        if ("PHAN_TRAM".equals(p.getLoaiGiam())) {
            giam = tamTinh.multiply(p.getGiaTriGiam()).divide(BigDecimal.valueOf(100), 0, BigDecimal.ROUND_HALF_UP);
            if (p.getGiamToiDa() != null && giam.compareTo(p.getGiamToiDa()) > 0) giam = p.getGiamToiDa();
        } else {
            giam = p.getGiaTriGiam();
        }
        if (giam.compareTo(tamTinh) > 0) giam = tamTinh;
        return KetQuaPhieuDTO.hopLe(p, giam.setScale(0, BigDecimal.ROUND_HALF_UP));
    }
}
