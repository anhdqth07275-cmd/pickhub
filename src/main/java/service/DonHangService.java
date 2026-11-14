package service;

import entity.HoaDon;
import entity.LichSuTrangThaiHoaDon;
import entity.NhanVien;
import enums.TrangThaiHoaDon;
import exception.NghiepVuException;
import util.GiaoDich;

import java.time.LocalDateTime;

/**
 * Nghiep vu xu ly don hang phia admin (module M7).
 * Da lam: xac nhan -> giao hang -> hoan thanh.
 * TODO (ban hoan thien): huy don kem hoan ton kho (ChiTietSanPham.soLuongTon),
 *      hoan luot khuyen mai (soLuongDaBan) va hoan phieu giam gia (SuDungPhieuGiamGia -> DA_HOAN).
 */
public class DonHangService {

    public void chuyenTrangThai(Long maHoaDon, String trangThaiMoiMa, Long maNhanVien) {
        final TrangThaiHoaDon moi = TrangThaiHoaDon.tuChuoi(trangThaiMoiMa);
        if (moi == null) throw new NghiepVuException("Trạng thái mới không hợp lệ");

        // note code nhu nay nhe ban: toan bo trong 1 transaction -> doi trang thai + ghi lich su cung thanh cong hoac cung that bai
        GiaoDich.chay(session -> {
            HoaDon hd = session.get(HoaDon.class, maHoaDon);
            if (hd == null) throw new NghiepVuException("Không tìm thấy hóa đơn");

            TrangThaiHoaDon cu = TrangThaiHoaDon.tuChuoi(hd.getTrangThai());
            if (cu == null || !cu.coTheChuyenSang(moi)) {
                throw new NghiepVuException("Không thể chuyển đơn từ " + hd.getTrangThai() + " sang " + moi);
            }

            LocalDateTime bayGio = LocalDateTime.now();
            hd.setTrangThai(moi.name());
            NhanVien nv = session.get(NhanVien.class, maNhanVien);

            // note code nhu nay nhe ban: CHECK CK_HoaDon_hoanThanh bat buoc HOAN_THANH phai co ngayGiaoThanhCong
            if (moi == TrangThaiHoaDon.DA_XAC_NHAN) {
                hd.setNhanVienXacNhan(nv);
                hd.setNgayXacNhan(bayGio);
            } else if (moi == TrangThaiHoaDon.HOAN_THANH) {
                hd.setNgayGiaoThanhCong(bayGio);
            }

            // note code nhu nay nhe ban: moi lan doi trang thai ghi 1 dong vao LichSuTrangThaiHoaDon (timeline)
            LichSuTrangThaiHoaDon ls = new LichSuTrangThaiHoaDon();
            ls.setMaHoaDon(maHoaDon);
            ls.setTuTrangThai(cu.name());
            ls.setDenTrangThai(moi.name());
            ls.setGhiChu("Nhân viên cập nhật trạng thái đơn hàng");
            ls.setNguoiThayDoi(nv);
            ls.setNgayThayDoi(bayGio);
            session.persist(ls);
            return null;
        });
    }
}
