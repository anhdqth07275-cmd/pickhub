package service;

import entity.HoaDon;
import entity.PhieuGiamGia;
import entity.SuDungPhieuGiamGia;
import exception.NghiepVuException;
import org.hibernate.Session;
import repository.PhieuGiamGiaRepository;
import repository.SuDungPhieuGiamGiaRepository;
import util.GiaoDich;

import java.time.LocalDateTime;

/**
 * Ghi nhan va hoan lai luot dung phieu giam gia (module M4).
 *  - ghiNhanSuDung: M6 goi khi dat hang
 *  - hoanLai      : M6 / M7 goi khi huy don
 * Moi ham co 2 ban:
 *  - ban nhan Session : dung khi M6 / M7 dang chay trong 1 transaction (de phieu va don hang cung commit / cung rollback)
 *  - ban khong Session: tu mo transaction rieng
 */
public class SuDungPhieuService {

    private final PhieuGiamGiaRepository phieuRepo = new PhieuGiamGiaRepository();
    private final SuDungPhieuGiamGiaRepository suDungRepo = new SuDungPhieuGiamGiaRepository();

    public void ghiNhanSuDung(final Long maHoaDon) {
        GiaoDich.chay(s -> {
            ghiNhanSuDung(s, maHoaDon);
            return null;
        });
    }

    public void hoanLai(final Long maHoaDon) {
        GiaoDich.chay(s -> {
            hoanLai(s, maHoaDon);
            return null;
        });
    }

    /**
     * Dat hang: hoa don co phieu thi tang soLanDaSuDung + them 1 dong SuDungPhieuGiamGia (DA_SU_DUNG).
     * Hoa don khong dung phieu thi bo qua.
     */
    public void ghiNhanSuDung(Session session, Long maHoaDon) {
        HoaDon hd = session.get(HoaDon.class, maHoaDon);
        if (hd == null) throw new NghiepVuException("Không tìm thấy hóa đơn");

        PhieuGiamGia phieu = hd.getPhieuGiamGia();
        if (phieu == null) return;      // don khong dung phieu

        // note code nhu nay nhe ban: UNIQUE maHoaDon - 1 hoa don toi da 1 luot dung phieu
        if (suDungRepo.timTheoHoaDon(session, maHoaDon) != null) {
            throw new NghiepVuException("Hóa đơn này đã ghi nhận phiếu giảm giá rồi");
        }

        // kiem tra lai gioi han moi nguoi ngay trong transaction (phong khach bam dat hang 2 lan)
        if (suDungRepo.demLuotDung(session, phieu.getMaPhieuGiamGia(), hd.getKhachHang().getMaKhachHang()) >= phieu.getGioiHanMoiNguoi()) {
            throw new NghiepVuException("Bạn đã dùng mã " + phieu.getMaGiamGia() + " tối đa " + phieu.getGioiHanMoiNguoi() + " lần");
        }

        // note code nhu nay nhe ban: UPDATE co dieu kien (soLanDaSuDung < gioiHanSuDung); 0 dong bi anh huong = vua het luot -> nem loi de M6 rollback
        if (!phieuRepo.tangSoLanDaSuDung(session, phieu.getMaPhieuGiamGia())) {
            throw new NghiepVuException("Mã giảm giá " + phieu.getMaGiamGia() + " đã hết lượt sử dụng");
        }

        SuDungPhieuGiamGia sd = new SuDungPhieuGiamGia();
        sd.setPhieuGiamGia(phieu);
        sd.setKhachHang(hd.getKhachHang());
        sd.setMaHoaDon(maHoaDon);
        sd.setSoTienGiam(hd.getSoTienGiam());
        sd.setNgaySuDung(hd.getNgayDatHang() != null ? hd.getNgayDatHang() : LocalDateTime.now());   // du lieu mau: ngaySuDung = ngayDatHang
        sd.setTrangThai("DA_SU_DUNG");
        session.persist(sd);
    }

    /**
     * Huy don: dong SuDungPhieuGiamGia chuyen DA_HOAN + ghi ngayHoanLai, tra lai 1 luot cho phieu.
     * Don khong dung phieu hoac da hoan roi thi bo qua (goi lai nhieu lan van an toan).
     */
    public void hoanLai(Session session, Long maHoaDon) {
        SuDungPhieuGiamGia sd = suDungRepo.timTheoHoaDon(session, maHoaDon);
        if (sd == null || "DA_HOAN".equals(sd.getTrangThai())) return;

        // note code nhu nay nhe ban: CHECK CK_SuDung_hoanLai bat buoc DA_HOAN phai co ngayHoanLai (va DA_SU_DUNG thi ngayHoanLai = null)
        sd.setTrangThai("DA_HOAN");
        sd.setNgayHoanLai(LocalDateTime.now());

        // tra lai 1 luot cho phieu -> khach (va nguoi khac) dung lai duoc
        phieuRepo.giamSoLanDaSuDung(session, sd.getPhieuGiamGia().getMaPhieuGiamGia());
    }
}
