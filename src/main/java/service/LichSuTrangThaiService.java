package service;

import dto.LichSuDongDTO;
import dto.LichSuLocDTO;
import dto.PhanTrang;
import entity.LichSuTrangThaiHoaDon;
import entity.NhanVien;
import enums.TrangThaiHoaDon;
import exception.NghiepVuException;
import org.hibernate.Session;
import repository.LichSuTrangThaiHoaDonRepository;
import repository.LichSuTrangThaiTraCuuRepository;
import util.GiaoDich;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Nghiep vu bang LichSuTrangThaiHoaDon (module M6): GHI lich su moi lan doi trang thai va TRA CUU lich su.
 * Bang nay chi-ghi-them: khong co ham sua / xoa.
 */
public class LichSuTrangThaiService {

    private final LichSuTrangThaiHoaDonRepository lichSuRepo = new LichSuTrangThaiHoaDonRepository();
    private final LichSuTrangThaiTraCuuRepository traCuuRepo = new LichSuTrangThaiTraCuuRepository();

    /**
     * GHI 1 dong lich su, dung CHUNG session / transaction cua noi goi.
     * Dung khi dang doi trang thai don trong 1 GiaoDich.chay(...): doi trang thai + ghi lich su cung thanh cong hoac cung rollback.
     *
     * tuTrangThai  : null = ban ghi khoi tao (vd khach tao gio hang)
     * nguoiThayDoi : null = khach hang / he thong thuc hien
     */
    public void ghiLichSu(Session session, Long maHoaDon, String tuTrangThai, String denTrangThai,
                          String ghiChu, NhanVien nguoiThayDoi) {
        if (maHoaDon == null) throw new NghiepVuException("Thiếu mã hóa đơn khi ghi lịch sử trạng thái");

        // note code nhu nay nhe ban: kiem tra trang thai co dung gia tri trong CHECK cua DB khong, de bao loi ro rang thay vi loi SQL kho hieu
        if (TrangThaiHoaDon.tuChuoi(denTrangThai) == null) {
            throw new NghiepVuException("Trạng thái đích không hợp lệ: " + denTrangThai);
        }
        if (tuTrangThai != null && TrangThaiHoaDon.tuChuoi(tuTrangThai) == null) {
            throw new NghiepVuException("Trạng thái nguồn không hợp lệ: " + tuTrangThai);
        }

        LichSuTrangThaiHoaDon ls = new LichSuTrangThaiHoaDon();
        ls.setMaHoaDon(maHoaDon);
        ls.setTuTrangThai(tuTrangThai);
        ls.setDenTrangThai(denTrangThai);
        ls.setGhiChu(catGhiChu(ghiChu));
        ls.setNguoiThayDoi(nguoiThayDoi);
        ls.setNgayThayDoi(LocalDateTime.now());
        session.persist(ls);
    }

    /**
     * GHI 1 dong lich su trong transaction RIENG (khi noi goi khong co session san).
     * maNhanVien = null neu do khach / he thong thuc hien.
     */
    public void ghiLichSu(final Long maHoaDon, final String tuTrangThai, final String denTrangThai,
                          final String ghiChu, final Long maNhanVien) {
        GiaoDich.chay(session -> {
            NhanVien nv = maNhanVien == null ? null : session.get(NhanVien.class, maNhanVien);
            ghiLichSu(session, maHoaDon, tuTrangThai, denTrangThai, ghiChu, nv);
            return null;
        });
    }

    /** Timeline cua 1 don, cu den moi (dung cho trang chi tiet don). */
    public List<LichSuTrangThaiHoaDon> layTimeline(Long maHoaDon) {
        return lichSuRepo.timTheoHoaDon(maHoaDon);
    }

    /** Tra cuu lich su tat ca don: loc theo ma tra cuu, trang thai, khoang ngay, nguoi thuc hien + phan trang. */
    public PhanTrang<LichSuDongDTO> timKiem(LichSuLocDTO loc) {
        return traCuuRepo.timKiem(loc);
    }

    // note code nhu nay nhe ban: cot ghiChu trong DB la NVARCHAR(255) -> ghi chu dai hon thi cat bot, khong de lam hong ca giao dich doi trang thai / dat hang chi vi ghi chu qua dai
    private String catGhiChu(String ghiChu) {
        if (ghiChu == null) return null;
        String g = ghiChu.trim();
        if (g.isEmpty()) return null;
        return g.length() > 255 ? g.substring(0, 255) : g;
    }
}
