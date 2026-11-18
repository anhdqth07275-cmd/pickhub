package com.pickhub.repository.impl;

import com.pickhub.dto.TrangHoaDonDTO;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.HoaDonChiTiet;
import com.pickhub.entity.KhachHang;
import com.pickhub.entity.LichSuTrangThaiHoaDon;
import com.pickhub.entity.NhanVien;
import com.pickhub.entity.PhieuGiamGia;
import com.pickhub.repository.IHoaDonRepository;
import com.pickhub.util.NghiepVuException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HoaDonRepository extends BaseRepository implements IHoaDonRepository {

    /**
     * Luu hoa don ban tai quay.
     * note code nhu nay nhe ban: TOAN BO (tru ton -> them hoa don -> them dong -> ghi lich su) nam trong MOT transaction.
     * Neu 1 mat hang het hang (UPDATE ... WHERE soLuongTon >= soLuong khong cap nhat duoc dong nao) thi nem loi va rollback het.
     */
    @Override
    public HoaDon luuHoaDonTaiQuay(HoaDon hoaDon, List<HoaDonChiTiet> danhSachDong, Long maNhanVien) {
        // sap xep dong theo ma mat hang de moi giao dich khoa dong ton kho theo CUNG thu tu -> tranh deadlock khi 2 may quay thanh toan cung luc
        List<HoaDonChiTiet> dongDaSapXep = new ArrayList<HoaDonChiTiet>(danhSachDong);
        Collections.sort(dongDaSapXep, new Comparator<HoaDonChiTiet>() {
            @Override
            public int compare(HoaDonChiTiet a, HoaDonChiTiet b) {
                return a.getMaChiTietSanPham().compareTo(b.getMaChiTietSanPham());
            }
        });

        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            LocalDateTime bayGio = LocalDateTime.now();

            // ---- 1. tru ton kho tung mat hang (nguyen tu: chi tru khi con du hang) ----
            for (HoaDonChiTiet dong : dongDaSapXep) {
                int soDong = session.createQuery(
                        "update ChiTietSanPham c set c.soLuongTon = c.soLuongTon - :sl, c.ngayCapNhat = :now "
                                + "where c.maChiTietSanPham = :id and c.soLuongTon >= :sl and c.trangThai = 'DANG_BAN'")
                        .setParameter("sl", dong.getSoLuong())
                        .setParameter("now", bayGio)
                        .setParameter("id", dong.getMaChiTietSanPham())
                        .executeUpdate();
                if (soDong == 0) {
                    throw new NghiepVuException("Mặt hàng \"" + dong.getTenSanPham() + "\" (" + dong.getMaHang() + ") không đủ số lượng hoặc đã ngừng bán");
                }
                // ton ve 0 thi chuyen trang thai HET_HANG de khop quy uoc cua he thong
                session.createQuery("update ChiTietSanPham c set c.trangThai = 'HET_HANG' "
                        + "where c.maChiTietSanPham = :id and c.soLuongTon = 0 and c.trangThai = 'DANG_BAN'")
                        .setParameter("id", dong.getMaChiTietSanPham()).executeUpdate();
            }

            // ---- 2. them hoa don (gan cac doi tuong lien quan lay tu CHINH session nay) ----
            hoaDon.setKhachHang(session.get(KhachHang.class, hoaDon.getKhachHang().getMaKhachHang()));
            NhanVien nhanVien = session.get(NhanVien.class, maNhanVien);
            hoaDon.setNhanVienXacNhan(nhanVien);
            if (hoaDon.getPhieuGiamGia() != null) {
                hoaDon.setPhieuGiamGia(session.get(PhieuGiamGia.class, hoaDon.getPhieuGiamGia().getMaPhieuGiamGia()));
            }
            session.persist(hoaDon);

            // ---- 3. them cac dong chi tiet ----
            for (HoaDonChiTiet dong : dongDaSapXep) {
                dong.setMaHoaDon(hoaDon.getMaHoaDon());
                session.persist(dong);
            }

            // ---- 4. ghi lich su trang thai (ban tai quay hoan thanh ngay) ----
            LichSuTrangThaiHoaDon lichSu = new LichSuTrangThaiHoaDon();
            lichSu.setMaHoaDon(hoaDon.getMaHoaDon());
            lichSu.setTuTrangThai(null);
            lichSu.setDenTrangThai("HOAN_THANH");
            lichSu.setGhiChu("Bán tại quầy");
            lichSu.setNguoiThayDoi(nhanVien);
            lichSu.setNgayThayDoi(bayGio);
            session.persist(lichSu);

            giaoDich.commit();
            return hoaDon;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public List<HoaDonChiTiet> timDongTheoHoaDon(Long maHoaDon) {
        Session session = moSession();
        try {
            return session.createQuery("from HoaDonChiTiet d where d.maHoaDon = :id order by d.maHoaDonChiTiet", HoaDonChiTiet.class)
                    .setParameter("id", maHoaDon).list();
        } finally {
            session.close();
        }
    }

    @Override
    public TrangHoaDonDTO timHoaDonTaiQuay(String tuKhoa, LocalDateTime tuNgay, LocalDateTime denNgay, Long maNhanVien, int trang, int kichThuoc) {
        StringBuilder dieuKien = new StringBuilder(" where h.kenhBan = 'TAI_QUAY' and h.ngayDatHang >= :tu and h.ngayDatHang < :den");
        boolean coTuKhoa = tuKhoa != null && !tuKhoa.trim().isEmpty();
        if (coTuKhoa) {
            dieuKien.append(" and (lower(h.maTraCuu) like :tk or lower(h.tenNguoiNhan) like :tk or h.soDienThoaiNguoiNhan like :tk)");
        }
        if (maNhanVien != null) {
            dieuKien.append(" and h.nhanVienXacNhan.maNhanVien = :nv");
        }
        Session session = moSession();
        try {
            Query<Long> demQuery = session.createQuery("select count(h) from HoaDon h" + dieuKien, Long.class);
            Query<HoaDon> dsQuery = session.createQuery("select h from HoaDon h" + dieuKien + " order by h.ngayDatHang desc", HoaDon.class);
            demQuery.setParameter("tu", tuNgay);
            demQuery.setParameter("den", denNgay);
            dsQuery.setParameter("tu", tuNgay);
            dsQuery.setParameter("den", denNgay);
            if (coTuKhoa) {
                String mau = "%" + tuKhoa.trim().toLowerCase() + "%";
                demQuery.setParameter("tk", mau);
                dsQuery.setParameter("tk", mau);
            }
            if (maNhanVien != null) {
                demQuery.setParameter("nv", maNhanVien);
                dsQuery.setParameter("nv", maNhanVien);
            }
            long tongSo = demQuery.uniqueResult();
            dsQuery.setFirstResult((Math.max(trang, 1) - 1) * kichThuoc);
            dsQuery.setMaxResults(kichThuoc);
            return new TrangHoaDonDTO(dsQuery.list(), tongSo);
        } finally {
            session.close();
        }
    }

    @Override
    public Object[] tongKetTaiQuay(LocalDateTime tuNgay, LocalDateTime denNgay, Long maNhanVien) {
        String hql = "select count(h), sum(h.tongTien) from HoaDon h where h.kenhBan = 'TAI_QUAY' and h.trangThai = 'HOAN_THANH' "
                + "and h.ngayDatHang >= :tu and h.ngayDatHang < :den" + (maNhanVien != null ? " and h.nhanVienXacNhan.maNhanVien = :nv" : "");
        Session session = moSession();
        try {
            Query<Object[]> query = session.createQuery(hql, Object[].class).setParameter("tu", tuNgay).setParameter("den", denNgay);
            if (maNhanVien != null) {
                query.setParameter("nv", maNhanVien);
            }
            return query.uniqueResult();
        } finally {
            session.close();
        }
    }
}
