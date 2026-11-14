package service;

import entity.HoaDon;
import entity.SuDungPhieuGiamGia;
import exception.NghiepVuException;
import org.hibernate.Session;

import java.time.LocalDateTime;

/**
 * Ghi nhan luot dung phieu giam gia khi dat hang (module M4).
 * TODO (M6/M7): hoanLai(hoaDon) khi huy don -> SuDungPhieuGiamGia DA_HOAN + ngayHoanLai, giam soLanDaSuDung.
 */
public class SuDungPhieuService {

    public void ghiNhanSuDung(Session s, HoaDon hd, LocalDateTime ngaySuDung) {
        Long maPhieu = hd.getPhieuGiamGia().getMaPhieuGiamGia();

        // note code nhu nay nhe ban: UPDATE co dieu kien -> 2 nguoi cung dung phieu con 1 luot thi chi 1 nguoi thanh cong (0 dong = het luot)
        int soDong = s.createQuery("update PhieuGiamGia p set p.soLanDaSuDung = p.soLanDaSuDung + 1, p.ngayCapNhat = :now "
                + "where p.maPhieuGiamGia = :id and (p.gioiHanSuDung is null or p.soLanDaSuDung < p.gioiHanSuDung)")
                .setParameter("now", ngaySuDung).setParameter("id", maPhieu).executeUpdate();
        if (soDong == 0) throw new NghiepVuException("Mã giảm giá vừa hết lượt sử dụng, vui lòng bỏ mã và đặt lại");

        SuDungPhieuGiamGia sd = new SuDungPhieuGiamGia();
        sd.setPhieuGiamGia(hd.getPhieuGiamGia());
        sd.setKhachHang(hd.getKhachHang());
        sd.setMaHoaDon(hd.getMaHoaDon());
        sd.setSoTienGiam(hd.getSoTienGiam());
        sd.setNgaySuDung(ngaySuDung);          // ngaySuDung = ngayDatHang (theo du lieu mau)
        sd.setTrangThai("DA_SU_DUNG");
        s.persist(sd);
    }
}
