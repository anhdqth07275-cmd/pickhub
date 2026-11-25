package com.pickhub.service.impl;

import com.pickhub.dto.BienTheBanDTO;
import com.pickhub.dto.BoLocBienTheDTO;
import com.pickhub.dto.KetQuaGiaDTO;
import com.pickhub.dto.TrangBienTheBanDTO;
import com.pickhub.dto.SanPhamBanDTO;
import com.pickhub.dto.TrangChiTietSanPhamDTO;
import com.pickhub.dto.TrangSanPhamBanDTO;
import com.pickhub.dto.TuyChonBoLocDTO;
import com.pickhub.entity.ChatLieu;
import com.pickhub.entity.ChiTietSanPham;
import com.pickhub.entity.ChuViTayCam;
import com.pickhub.entity.DanhMuc;
import com.pickhub.entity.KhoiLuong;
import com.pickhub.entity.MauSac;
import com.pickhub.entity.ThuongHieu;
import com.pickhub.repository.IChiTietSanPhamRepository;
import com.pickhub.repository.IDanhMucRepository;
import com.pickhub.repository.IHinhAnhSanPhamRepository;
import com.pickhub.repository.impl.ChiTietSanPhamRepository;
import com.pickhub.repository.impl.DanhMucRepository;
import com.pickhub.repository.impl.HinhAnhSanPhamRepository;
import com.pickhub.service.IKhuyenMaiService;
import com.pickhub.service.ISanPhamService;
import com.pickhub.util.MoTaBienTheUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SanPhamServiceImpl extends BaseServiceImpl implements ISanPhamService {

    public static final String ANH_MAC_DINH = "/assets/no-image.png";

    private final IChiTietSanPhamRepository chiTietSanPhamRepository = new ChiTietSanPhamRepository();
    private final IHinhAnhSanPhamRepository hinhAnhRepository = new HinhAnhSanPhamRepository();
    private final IDanhMucRepository danhMucRepository = new DanhMucRepository();
    private final IKhuyenMaiService khuyenMaiService = new KhuyenMaiServiceImpl();

    @Override
    public TrangBienTheBanDTO timKiemBienThe(BoLocBienTheDTO boLoc) {
        TrangChiTietSanPhamDTO trang = chiTietSanPhamRepository.timKiemBienThe(boLoc);
        return new TrangBienTheBanDTO(taoDanhSachHienThi(trang.getDanhSach()), trang.getTongSo(), boLoc.getTrang(), boLoc.getKichThuoc());
    }

    @Override
    public TrangSanPhamBanDTO timKiemSanPham(BoLocBienTheDTO boLoc) {
        // note code nhu nay nhe ban: 3 buoc
        //   1) lay ma cac SAN PHAM cua trang hien tai (+ tong so san pham de tinh so trang)
        //   2) lay TAT CA bien the cua cac san pham do (ke ca bien the het hang de popup chon hien du)
        //   3) gom bien the lai theo san pham, GIU DUNG thu tu cua buoc 1
        List<Long> maSanPham = chiTietSanPhamRepository.timMaSanPham(boLoc);
        long tongSo = chiTietSanPhamRepository.demSanPham(boLoc);
        // taoDanhSachHienThi co san: gan anh + gia sau khuyen mai cho tung bien the
        List<BienTheBanDTO> tatCaBienThe = taoDanhSachHienThi(chiTietSanPhamRepository.timBienTheTheoSanPham(maSanPham));

        Map<Long, List<BienTheBanDTO>> theoSanPham = new LinkedHashMap<Long, List<BienTheBanDTO>>();
        for (Long ma : maSanPham) {
            theoSanPham.put(ma, new ArrayList<BienTheBanDTO>());
        }
        for (BienTheBanDTO bienThe : tatCaBienThe) {
            List<BienTheBanDTO> nhom = theoSanPham.get(bienThe.getMaSanPham());
            if (nhom != null) {
                nhom.add(bienThe);
            }
        }
        List<SanPhamBanDTO> danhSach = new ArrayList<SanPhamBanDTO>();
        for (List<BienTheBanDTO> nhom : theoSanPham.values()) {
            if (!nhom.isEmpty()) {
                danhSach.add(new SanPhamBanDTO(nhom));
            }
        }
        return new TrangSanPhamBanDTO(danhSach, tongSo, boLoc.getTrang(), boLoc.getKichThuoc());
    }

    @Override
    public BienTheBanDTO timTheoMaHang(String maHang) {
        if (maHang == null || maHang.trim().isEmpty()) {
            return null;
        }
        ChiTietSanPham matHang = chiTietSanPhamRepository.timTheoMaHang(maHang);
        if (matHang == null || "NGUNG_BAN".equals(matHang.getTrangThai())) {
            return null;
        }
        List<ChiTietSanPham> mot = new ArrayList<ChiTietSanPham>();
        mot.add(matHang);
        return taoDanhSachHienThi(mot).get(0);
    }

    /** Ghep thong tin hien thi: anh (rieng cua mat hang, neu khong co thi anh chinh cua san pham) va gia sau khuyen mai. */
    private List<BienTheBanDTO> taoDanhSachHienThi(List<ChiTietSanPham> danhSach) {
        List<Long> maBienThe = new ArrayList<Long>();
        List<Long> maSanPham = new ArrayList<Long>();
        for (ChiTietSanPham matHang : danhSach) {
            maBienThe.add(matHang.getMaChiTietSanPham());
            if (!maSanPham.contains(matHang.getSanPham().getMaSanPham())) {
                maSanPham.add(matHang.getSanPham().getMaSanPham());
            }
        }
        Map<Long, String> anhChinh = hinhAnhRepository.layAnhChinhTheoSanPham(maSanPham);
        Map<Long, String> anhRieng = hinhAnhRepository.layAnhRiengTheoBienThe(maBienThe);
        Map<Long, KetQuaGiaDTO> bangGia = khuyenMaiService.tinhGiaNhieuMatHang(danhSach);

        List<BienTheBanDTO> ketQua = new ArrayList<BienTheBanDTO>();
        for (ChiTietSanPham matHang : danhSach) {
            KetQuaGiaDTO gia = bangGia.get(matHang.getMaChiTietSanPham());
            BienTheBanDTO dong = new BienTheBanDTO();
            dong.setMaChiTietSanPham(matHang.getMaChiTietSanPham());
            dong.setMaSanPham(matHang.getSanPham().getMaSanPham());
            dong.setMaHang(matHang.getMaHang());
            dong.setTenSanPham(matHang.getSanPham().getTenSanPham());
            dong.setThuongHieu(matHang.getSanPham().getThuongHieu().getTenThuongHieu());
            dong.setDanhMuc(matHang.getSanPham().getDanhMuc().getTenDanhMuc());
            dong.setMoTaBienThe(MoTaBienTheUtil.moTa(matHang));
            String anh = anhRieng.get(matHang.getMaChiTietSanPham());
            if (anh == null) {
                anh = anhChinh.get(matHang.getSanPham().getMaSanPham());
            }
            dong.setAnh(anh == null ? ANH_MAC_DINH : anh);
            dong.setGiaGoc(gia.getGiaGoc());
            dong.setGiaBan(gia.getGiaBan());
            dong.setMaChiTietChuongTrinhKhuyenMai(gia.getMaChiTietChuongTrinhKhuyenMai());
            dong.setSoLuongTon(matHang.getSoLuongTon());
            dong.setNguongCanhBao(matHang.getNguongCanhBaoTonKho());
            dong.setTrangThai(matHang.getTrangThai());
            ketQua.add(dong);
        }
        return ketQua;
    }

    @Override
    public TuyChonBoLocDTO layTuyChonBoLoc() {
        TuyChonBoLocDTO tuyChon = new TuyChonBoLocDTO();

        for (DanhMuc danhMuc : danhMucRepository.layDangHoatDong()) {
            if (danhMuc.getDanhMucCha() == null) {
                tuyChon.getDanhMucGoc().add(danhMuc);
            } else {
                tuyChon.getDanhMucCon().add(danhMuc);
            }
        }
        for (ThuongHieu thuongHieu : layTatCaThuongHieu()) {
            if ("HOAT_DONG".equals(thuongHieu.getTrangThai())) {
                tuyChon.getThuongHieu().add(thuongHieu);
            }
        }
        for (MauSac mauSac : layTatCaMauSac()) {
            if ("HOAT_DONG".equals(mauSac.getTrangThai())) {
                tuyChon.getMauSac().add(mauSac);
            }
        }
        for (KhoiLuong khoiLuong : layTatCaKhoiLuong()) {
            if ("HOAT_DONG".equals(khoiLuong.getTrangThai())) {
                tuyChon.getKhoiLuong().add(khoiLuong);
            }
        }
        for (ChatLieu chatLieu : layTatCaChatLieu()) {
            if ("HOAT_DONG".equals(chatLieu.getTrangThai())) {
                tuyChon.getChatLieu().add(chatLieu);
            }
        }
        for (ChuViTayCam chuVi : layTatCaChuViTayCam()) {
            if ("HOAT_DONG".equals(chuVi.getTrangThai())) {
                tuyChon.getChuVi().add(chuVi);
            }
        }
        // sap xep de nhan vien de tim: thuong hieu / mau / chat lieu theo ten, khoi luong va chu vi tang dan
        Collections.sort(tuyChon.getThuongHieu(), new Comparator<ThuongHieu>() {
            @Override
            public int compare(ThuongHieu a, ThuongHieu b) {
                return a.getTenThuongHieu().compareToIgnoreCase(b.getTenThuongHieu());
            }
        });
        Collections.sort(tuyChon.getMauSac(), new Comparator<MauSac>() {
            @Override
            public int compare(MauSac a, MauSac b) {
                return a.getTenMauSac().compareToIgnoreCase(b.getTenMauSac());
            }
        });
        Collections.sort(tuyChon.getChatLieu(), new Comparator<ChatLieu>() {
            @Override
            public int compare(ChatLieu a, ChatLieu b) {
                return a.getTenChatLieu().compareToIgnoreCase(b.getTenChatLieu());
            }
        });
        Collections.sort(tuyChon.getKhoiLuong(), new Comparator<KhoiLuong>() {
            @Override
            public int compare(KhoiLuong a, KhoiLuong b) {
                return a.getKhoiLuongGram().compareTo(b.getKhoiLuongGram());
            }
        });
        Collections.sort(tuyChon.getChuVi(), new Comparator<ChuViTayCam>() {
            @Override
            public int compare(ChuViTayCam a, ChuViTayCam b) {
                return a.getChuViInch().compareTo(b.getChuViInch());
            }
        });
        return tuyChon;
    }
}
