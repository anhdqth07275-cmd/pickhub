package com.pickhub.repository.impl;

import com.pickhub.dto.BoLocBienTheDTO;
import com.pickhub.dto.TrangChiTietSanPhamDTO;
import com.pickhub.entity.ChiTietSanPham;
import com.pickhub.repository.IChiTietSanPhamRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChiTietSanPhamRepository extends BaseRepository implements IChiTietSanPhamRepository {

    /**
     * BO LOC SAN PHAM NANG CAO (muc bien the).
     * note code nhu nay nhe ban: dieu kien nao nhan vien co chon moi duoc ghep them vao WHERE bang AND, nen ket hop duoc nhieu dieu kien cung luc.
     * Moi gia tri di qua tham so (:tk0, :dm, :th...) nen khong bi tan cong HQL injection. Cau count dung CHUNG phan WHERE de tong so luon khop.
     */
    @Override
    public TrangChiTietSanPhamDTO timKiemBienThe(BoLocBienTheDTO boLoc) {
        StringBuilder dieuKien = new StringBuilder(" where 1 = 1");
        List<String> tenThamSo = new ArrayList<String>();
        List<Object> giaTriThamSo = new ArrayList<Object>();

        // ---- 1. tu khoa: tach thanh nhieu tu, MOI tu phai khop ten san pham, ma hang hoac ten thuong hieu ----
        if (boLoc.getTuKhoa() != null && !boLoc.getTuKhoa().trim().isEmpty()) {
            String[] cacTu = boLoc.getTuKhoa().trim().toLowerCase().split("\\s+");
            for (int i = 0; i < cacTu.length && i < 6; i++) {
                String ten = "tk" + i;
                dieuKien.append(" and (lower(c.sanPham.tenSanPham) like :").append(ten)
                        .append(" or lower(c.maHang) like :").append(ten)
                        .append(" or lower(c.sanPham.thuongHieu.tenThuongHieu) like :").append(ten).append(")");
                tenThamSo.add(ten);
                giaTriThamSo.add("%" + cacTu[i] + "%");
            }
        }
        // ---- 2. danh muc: chon danh muc CHA thi lay ca san pham thuoc danh muc CON ----
        if (boLoc.getMaDanhMuc() != null) {
            dieuKien.append(" and (c.sanPham.danhMuc.maDanhMuc = :dm or c.sanPham.danhMuc.danhMucCha.maDanhMuc = :dm)");
            tenThamSo.add("dm");
            giaTriThamSo.add(boLoc.getMaDanhMuc());
        }
        // ---- 3. thuong hieu, loai san pham ----
        if (boLoc.getMaThuongHieu() != null) {
            dieuKien.append(" and c.sanPham.thuongHieu.maThuongHieu = :th");
            tenThamSo.add("th");
            giaTriThamSo.add(boLoc.getMaThuongHieu());
        }
        if (boLoc.getLoaiSanPham() != null && !boLoc.getLoaiSanPham().trim().isEmpty()) {
            dieuKien.append(" and c.sanPham.loaiSanPham = :loai");
            tenThamSo.add("loai");
            giaTriThamSo.add(boLoc.getLoaiSanPham().trim());
        }
        // ---- 4. thuoc tinh bien the: mau, khoi luong, chat lieu, chu vi tay cam ----
        if (boLoc.getMaMauSac() != null) {
            dieuKien.append(" and c.mauSac.maMauSac = :ms");
            tenThamSo.add("ms");
            giaTriThamSo.add(boLoc.getMaMauSac());
        }
        if (boLoc.getMaKhoiLuong() != null) {
            dieuKien.append(" and c.khoiLuong.maKhoiLuong = :kl");
            tenThamSo.add("kl");
            giaTriThamSo.add(boLoc.getMaKhoiLuong());
        }
        if (boLoc.getMaChatLieu() != null) {
            dieuKien.append(" and c.chatLieu.maChatLieu = :cl");
            tenThamSo.add("cl");
            giaTriThamSo.add(boLoc.getMaChatLieu());
        }
        if (boLoc.getMaChuViTayCam() != null) {
            dieuKien.append(" and c.chuViTayCam.maChuViTayCam = :cv");
            tenThamSo.add("cv");
            giaTriThamSo.add(boLoc.getMaChuViTayCam());
        }
        // ---- 5. khoang gia (theo gia goc cua mat hang) ----
        if (boLoc.getGiaTu() != null) {
            dieuKien.append(" and c.giaGoc >= :giaTu");
            tenThamSo.add("giaTu");
            giaTriThamSo.add(boLoc.getGiaTu());
        }
        if (boLoc.getGiaDen() != null) {
            dieuKien.append(" and c.giaGoc <= :giaDen");
            tenThamSo.add("giaDen");
            giaTriThamSo.add(boLoc.getGiaDen());
        }
        // ---- 6. tinh trang ton kho (chuoi do CODE quy dinh, khong lay nguyen van tu nguoi dung) ----
        String tonKho = boLoc.getTinhTrangTon() == null ? "CON_HANG" : boLoc.getTinhTrangTon();
        if ("CON_HANG".equals(tonKho)) {
            dieuKien.append(" and c.soLuongTon > 0 and c.trangThai = 'DANG_BAN'");
        } else if ("SAP_HET".equals(tonKho)) {
            dieuKien.append(" and c.soLuongTon > 0 and c.soLuongTon <= c.nguongCanhBaoTonKho and c.trangThai = 'DANG_BAN'");
        } else if ("HET_HANG".equals(tonKho)) {
            dieuKien.append(" and c.soLuongTon = 0 and c.trangThai <> 'NGUNG_BAN'");
        } else {
            dieuKien.append(" and c.trangThai <> 'NGUNG_BAN'");         // TAT_CA: van bo mat hang da ngung ban
        }
        // ---- 7. chi mat hang dang co khuyen mai ----
        if (boLoc.isGoiYKhuyenMai()) {
            dieuKien.append(" and exists (select 1 from ChiTietChuongTrinhKhuyenMai k, ChuongTrinhKhuyenMai p"
                    + " where k.chiTietSanPham = c and k.maChuongTrinhKhuyenMai = p.maChuongTrinhKhuyenMai"
                    + " and p.trangThai = 'DANG_CHAY' and p.ngayBatDau <= :bayGio and p.ngayKetThuc >= :bayGio)");
            tenThamSo.add("bayGio");
            giaTriThamSo.add(LocalDateTime.now());
        }

        // ---- 8. sap xep (chuoi cung do CODE chon) ----
        String sapXep = boLoc.getSapXep() == null ? "ten" : boLoc.getSapXep();
        String orderBy;
        if ("gia_tang".equals(sapXep)) {
            orderBy = " order by c.giaGoc asc, c.maChiTietSanPham";
        } else if ("gia_giam".equals(sapXep)) {
            orderBy = " order by c.giaGoc desc, c.maChiTietSanPham";
        } else if ("moi".equals(sapXep)) {
            orderBy = " order by c.maChiTietSanPham desc";
        } else if ("ton_it".equals(sapXep)) {
            orderBy = " order by c.soLuongTon asc, c.maChiTietSanPham";
        } else if ("ton_nhieu".equals(sapXep)) {
            orderBy = " order by c.soLuongTon desc, c.maChiTietSanPham";
        } else {
            orderBy = " order by c.sanPham.tenSanPham asc, c.maHang asc";
        }

        Session session = moSession();
        try {
            Query<Long> demQuery = session.createQuery("select count(c) from ChiTietSanPham c" + dieuKien, Long.class);
            Query<ChiTietSanPham> dsQuery = session.createQuery("select c from ChiTietSanPham c" + dieuKien + orderBy, ChiTietSanPham.class);
            for (int i = 0; i < tenThamSo.size(); i++) {
                demQuery.setParameter(tenThamSo.get(i), giaTriThamSo.get(i));
                dsQuery.setParameter(tenThamSo.get(i), giaTriThamSo.get(i));
            }
            long tongSo = demQuery.uniqueResult();
            dsQuery.setFirstResult((boLoc.getTrang() - 1) * boLoc.getKichThuoc());
            dsQuery.setMaxResults(boLoc.getKichThuoc());
            return new TrangChiTietSanPhamDTO(dsQuery.list(), tongSo);
        } finally {
            session.close();
        }
    }

    @Override
    public ChiTietSanPham timTheoMaHang(String maHang) {
        Session session = moSession();
        try {
            return session.createQuery("from ChiTietSanPham c where lower(c.maHang) = :ma", ChiTietSanPham.class)
                    .setParameter("ma", maHang.trim().toLowerCase()).uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public List<ChiTietSanPham> timTheoDanhSachId(List<Long> danhSachMa) {
        if (danhSachMa == null || danhSachMa.isEmpty()) {
            return new ArrayList<ChiTietSanPham>();
        }
        Session session = moSession();
        try {
            return session.createQuery("from ChiTietSanPham c where c.maChiTietSanPham in (:ids)", ChiTietSanPham.class)
                    .setParameterList("ids", danhSachMa).list();
        } finally {
            session.close();
        }
    }

    // =====================================================================
    // TIM THEO SAN PHAM (the san pham tren man hinh ban hang)
    // note code nhu nay nhe ban: gom cac bien the cung 1 san pham thanh 1 the. Phan trang theo SAN PHAM nen
    // moi trang du N the san pham (khong bi 1 san pham tach lam doi o 2 trang).
    // =====================================================================

    /**
     * Dieu kien WHERE cua bo loc, DUNG CHUNG cho timMaSanPham va demSanPham.
     * note code nhu nay nhe ban: phan loc nay giong het phan loc trong timKiemBienThe o tren (cung quy tac, cung tham so).
     * Minh chep ra thay vi sua timKiemBienThe de khong dung toi ham dang chay on dinh; neu sau nay doi bo loc thi nho sua ca 2 cho.
     */
    private String taoDieuKienLoc(BoLocBienTheDTO boLoc, List<String> tenThamSo, List<Object> giaTriThamSo) {
        StringBuilder dieuKien = new StringBuilder(" where 1 = 1");

        // ---- 1. tu khoa: tach thanh nhieu tu, MOI tu phai khop ten san pham, ma hang hoac ten thuong hieu ----
        if (boLoc.getTuKhoa() != null && !boLoc.getTuKhoa().trim().isEmpty()) {
            String[] cacTu = boLoc.getTuKhoa().trim().toLowerCase().split("\\s+");
            for (int i = 0; i < cacTu.length && i < 6; i++) {
                String ten = "tk" + i;
                dieuKien.append(" and (lower(c.sanPham.tenSanPham) like :").append(ten)
                        .append(" or lower(c.maHang) like :").append(ten)
                        .append(" or lower(c.sanPham.thuongHieu.tenThuongHieu) like :").append(ten).append(")");
                tenThamSo.add(ten);
                giaTriThamSo.add("%" + cacTu[i] + "%");
            }
        }
        // ---- 2. danh muc: chon danh muc CHA thi lay ca san pham thuoc danh muc CON ----
        if (boLoc.getMaDanhMuc() != null) {
            dieuKien.append(" and (c.sanPham.danhMuc.maDanhMuc = :dm or c.sanPham.danhMuc.danhMucCha.maDanhMuc = :dm)");
            tenThamSo.add("dm");
            giaTriThamSo.add(boLoc.getMaDanhMuc());
        }
        // ---- 3. thuong hieu, loai san pham ----
        if (boLoc.getMaThuongHieu() != null) {
            dieuKien.append(" and c.sanPham.thuongHieu.maThuongHieu = :th");
            tenThamSo.add("th");
            giaTriThamSo.add(boLoc.getMaThuongHieu());
        }
        if (boLoc.getLoaiSanPham() != null && !boLoc.getLoaiSanPham().trim().isEmpty()) {
            dieuKien.append(" and c.sanPham.loaiSanPham = :loai");
            tenThamSo.add("loai");
            giaTriThamSo.add(boLoc.getLoaiSanPham().trim());
        }
        // ---- 4. thuoc tinh bien the: mau, khoi luong, chat lieu, chu vi tay cam ----
        if (boLoc.getMaMauSac() != null) {
            dieuKien.append(" and c.mauSac.maMauSac = :ms");
            tenThamSo.add("ms");
            giaTriThamSo.add(boLoc.getMaMauSac());
        }
        if (boLoc.getMaKhoiLuong() != null) {
            dieuKien.append(" and c.khoiLuong.maKhoiLuong = :kl");
            tenThamSo.add("kl");
            giaTriThamSo.add(boLoc.getMaKhoiLuong());
        }
        if (boLoc.getMaChatLieu() != null) {
            dieuKien.append(" and c.chatLieu.maChatLieu = :cl");
            tenThamSo.add("cl");
            giaTriThamSo.add(boLoc.getMaChatLieu());
        }
        if (boLoc.getMaChuViTayCam() != null) {
            dieuKien.append(" and c.chuViTayCam.maChuViTayCam = :cv");
            tenThamSo.add("cv");
            giaTriThamSo.add(boLoc.getMaChuViTayCam());
        }
        // ---- 5. khoang gia (theo gia goc cua bien the) ----
        if (boLoc.getGiaTu() != null) {
            dieuKien.append(" and c.giaGoc >= :giaTu");
            tenThamSo.add("giaTu");
            giaTriThamSo.add(boLoc.getGiaTu());
        }
        if (boLoc.getGiaDen() != null) {
            dieuKien.append(" and c.giaGoc <= :giaDen");
            tenThamSo.add("giaDen");
            giaTriThamSo.add(boLoc.getGiaDen());
        }
        // ---- 6. tinh trang ton kho (chuoi do CODE quy dinh, khong lay nguyen van tu nguoi dung) ----
        String tonKho = boLoc.getTinhTrangTon() == null ? "CON_HANG" : boLoc.getTinhTrangTon();
        if ("CON_HANG".equals(tonKho)) {
            dieuKien.append(" and c.soLuongTon > 0 and c.trangThai = 'DANG_BAN'");
        } else if ("SAP_HET".equals(tonKho)) {
            dieuKien.append(" and c.soLuongTon > 0 and c.soLuongTon <= c.nguongCanhBaoTonKho and c.trangThai = 'DANG_BAN'");
        } else if ("HET_HANG".equals(tonKho)) {
            dieuKien.append(" and c.soLuongTon = 0 and c.trangThai <> 'NGUNG_BAN'");
        } else {
            dieuKien.append(" and c.trangThai <> 'NGUNG_BAN'");         // TAT_CA: van bo mat hang da ngung ban
        }
        // ---- 7. chi bien the dang co khuyen mai ----
        if (boLoc.isGoiYKhuyenMai()) {
            dieuKien.append(" and exists (select 1 from ChiTietChuongTrinhKhuyenMai k, ChuongTrinhKhuyenMai p"
                    + " where k.chiTietSanPham = c and k.maChuongTrinhKhuyenMai = p.maChuongTrinhKhuyenMai"
                    + " and p.trangThai = 'DANG_CHAY' and p.ngayBatDau <= :bayGio and p.ngayKetThuc >= :bayGio)");
            tenThamSo.add("bayGio");
            giaTriThamSo.add(LocalDateTime.now());
        }
        return dieuKien.toString();
    }

    @Override
    public List<Long> timMaSanPham(BoLocBienTheDTO boLoc) {
        List<String> tenThamSo = new ArrayList<String>();
        List<Object> giaTriThamSo = new ArrayList<Object>();
        String dieuKien = taoDieuKienLoc(boLoc, tenThamSo, giaTriThamSo);

        // note code nhu nay nhe ban: da GOM theo san pham (group by) nen sap xep theo gia / ton phai dung ham gop
        // min / max / sum tren cac bien the cua san pham. Chuoi order by do CODE chon, khong lay tu nguoi dung.
        String sapXep = boLoc.getSapXep() == null ? "ten" : boLoc.getSapXep();
        String orderBy;
        if ("gia_tang".equals(sapXep)) {
            orderBy = " order by min(c.giaGoc) asc, c.sanPham.maSanPham";
        } else if ("gia_giam".equals(sapXep)) {
            orderBy = " order by max(c.giaGoc) desc, c.sanPham.maSanPham";
        } else if ("moi".equals(sapXep)) {
            orderBy = " order by c.sanPham.maSanPham desc";
        } else if ("ton_it".equals(sapXep)) {
            orderBy = " order by sum(c.soLuongTon) asc, c.sanPham.maSanPham";
        } else if ("ton_nhieu".equals(sapXep)) {
            orderBy = " order by sum(c.soLuongTon) desc, c.sanPham.maSanPham";
        } else {
            orderBy = " order by c.sanPham.tenSanPham asc, c.sanPham.maSanPham asc";
        }

        Session session = moSession();
        try {
            Query<Long> truyVan = session.createQuery("select c.sanPham.maSanPham from ChiTietSanPham c" + dieuKien
                    + " group by c.sanPham.maSanPham, c.sanPham.tenSanPham" + orderBy, Long.class);
            for (int i = 0; i < tenThamSo.size(); i++) {
                truyVan.setParameter(tenThamSo.get(i), giaTriThamSo.get(i));
            }
            truyVan.setFirstResult((boLoc.getTrang() - 1) * boLoc.getKichThuoc());
            truyVan.setMaxResults(boLoc.getKichThuoc());
            return truyVan.list();
        } finally {
            session.close();
        }
    }

    @Override
    public long demSanPham(BoLocBienTheDTO boLoc) {
        List<String> tenThamSo = new ArrayList<String>();
        List<Object> giaTriThamSo = new ArrayList<Object>();
        String dieuKien = taoDieuKienLoc(boLoc, tenThamSo, giaTriThamSo);
        Session session = moSession();
        try {
            Query<Long> truyVan = session.createQuery("select count(distinct c.sanPham.maSanPham) from ChiTietSanPham c" + dieuKien, Long.class);
            for (int i = 0; i < tenThamSo.size(); i++) {
                truyVan.setParameter(tenThamSo.get(i), giaTriThamSo.get(i));
            }
            return truyVan.uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public List<ChiTietSanPham> timBienTheTheoSanPham(List<Long> danhSachMaSanPham) {
        if (danhSachMaSanPham == null || danhSachMaSanPham.isEmpty()) {
            return new ArrayList<ChiTietSanPham>();
        }
        Session session = moSession();
        try {
            // lay ca bien the HET HANG de popup hien du phan loai (bien the het hang se bi lam xam, khong chon duoc)
            return session.createQuery("from ChiTietSanPham c where c.sanPham.maSanPham in (:ids) and c.trangThai <> 'NGUNG_BAN'"
                    + " order by c.sanPham.tenSanPham asc, c.maHang asc", ChiTietSanPham.class)
                    .setParameterList("ids", danhSachMaSanPham).list();
        } finally {
            session.close();
        }
    }
}
