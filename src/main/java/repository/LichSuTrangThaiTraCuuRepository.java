package repository;

import dto.LichSuDongDTO;
import dto.LichSuLocDTO;
import dto.PhanTrang;
import entity.HoaDon;
import entity.LichSuTrangThaiHoaDon;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Truy van TRA CUU lich su trang thai (module M6): loc + phan trang + kem ma tra cuu cua don.
 * Viet o lop rieng de khong dong vao LichSuTrangThaiHoaDonRepository (tranh xung dot code voi cac module khac).
 */
public class LichSuTrangThaiTraCuuRepository extends BaseRepository<LichSuTrangThaiHoaDon> {

    public LichSuTrangThaiTraCuuRepository() {
        super(LichSuTrangThaiHoaDon.class, "maLichSuTrangThaiHoaDon");
    }

    public PhanTrang<LichSuDongDTO> timKiem(LichSuLocDTO loc) {
        // note code nhu nay nhe ban: LichSuTrangThaiHoaDon chi giu maHoaDon (khong co quan he), nen noi voi HoaDon bang dieu kien l.maHoaDon = h.maHoaDon de lay maTraCuu
        StringBuilder dk = new StringBuilder(" from HoaDon h, LichSuTrangThaiHoaDon l where l.maHoaDon = h.maHoaDon");
        boolean coTuKhoa = loc.tuKhoa != null && !loc.tuKhoa.trim().isEmpty();
        boolean coTrangThai = loc.denTrangThai != null && !loc.denTrangThai.trim().isEmpty();

        if (coTuKhoa) dk.append(" and lower(h.maTraCuu) like :tk");
        // note code nhu nay nhe ban: dong "khoi tao gio hang" (GIO_HANG) rat nhieu va khong co y nghia xu ly don -> mac dinh an, chi hien khi chon loc "Gio hang"
        if (coTrangThai) dk.append(" and l.denTrangThai = :tt");
        else dk.append(" and l.denTrangThai <> 'GIO_HANG'");
        if (loc.tuNgay != null) dk.append(" and l.ngayThayDoi >= :tu");
        if (loc.denNgay != null) dk.append(" and l.ngayThayDoi < :den");      // den het ngay -> nho hon 0h ngay hom sau
        // note code nhu nay nhe ban: nguoiThayDoi = NULL nghia la do khach hang / he thong thuc hien
        if ("NHAN_VIEN".equals(loc.nguoiThucHien)) dk.append(" and l.nguoiThayDoi is not null");
        if ("HE_THONG".equals(loc.nguoiThucHien)) dk.append(" and l.nguoiThayDoi is null");

        try (Session s = moSession()) {
            Query<Long> demQ = s.createQuery("select count(l)" + dk, Long.class);
            Query<Object[]> listQ = s.createQuery("select l, h" + dk
                    + " order by l.ngayThayDoi desc, l.maLichSuTrangThaiHoaDon desc", Object[].class);
            datThamSo(demQ, loc, coTuKhoa, coTrangThai);
            datThamSo(listQ, loc, coTuKhoa, coTrangThai);

            long tongSo = demQ.uniqueResult();
            listQ.setFirstResult((loc.trang - 1) * loc.kichThuoc);      // bo qua cac dong cua trang truoc
            listQ.setMaxResults(loc.kichThuoc);

            List<LichSuDongDTO> ds = new ArrayList<LichSuDongDTO>();
            for (Object[] dong : listQ.list()) {
                LichSuTrangThaiHoaDon l = (LichSuTrangThaiHoaDon) dong[0];
                HoaDon h = (HoaDon) dong[1];
                String tenNguoi = l.getNguoiThayDoi() == null ? null : l.getNguoiThayDoi().getHoTen();
                ds.add(new LichSuDongDTO(l.getMaHoaDon(), h.getMaTraCuu(), l.getTuTrangThai(), l.getDenTrangThai(),
                        l.getGhiChu(), tenNguoi, l.getNgayThayDoi()));
            }
            return new PhanTrang<LichSuDongDTO>(ds, loc.trang, loc.kichThuoc, tongSo);
        }
    }

    // gan gia tri cho cac tham so (dung chung cho cau dem va cau lay danh sach)
    private void datThamSo(Query<?> q, LichSuLocDTO loc, boolean coTuKhoa, boolean coTrangThai) {
        if (coTuKhoa) q.setParameter("tk", "%" + loc.tuKhoa.trim().toLowerCase() + "%");
        if (coTrangThai) q.setParameter("tt", loc.denTrangThai.trim());
        if (loc.tuNgay != null) q.setParameter("tu", loc.tuNgay.atStartOfDay());
        if (loc.denNgay != null) q.setParameter("den", loc.denNgay.plusDays(1).atStartOfDay());
    }
}
