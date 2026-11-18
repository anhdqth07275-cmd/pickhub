package com.pickhub.service.impl;

import com.pickhub.dto.KetQuaGiaDTO;
import com.pickhub.entity.ChiTietChuongTrinhKhuyenMai;
import com.pickhub.entity.ChiTietSanPham;
import com.pickhub.repository.IKhuyenMaiRepository;
import com.pickhub.repository.impl.KhuyenMaiRepository;
import com.pickhub.service.IKhuyenMaiService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhuyenMaiServiceImpl extends BaseServiceImpl implements IKhuyenMaiService {

    private final IKhuyenMaiRepository khuyenMaiRepository = new KhuyenMaiRepository();

    @Override
    public Map<Long, KetQuaGiaDTO> tinhGiaNhieuMatHang(List<ChiTietSanPham> danhSach) {
        List<Long> danhSachMa = new ArrayList<Long>();
        for (ChiTietSanPham matHang : danhSach) {
            danhSachMa.add(matHang.getMaChiTietSanPham());
        }
        Map<Long, ChiTietChuongTrinhKhuyenMai> khuyenMai = khuyenMaiRepository.timKhuyenMaiDangChay(danhSachMa, LocalDateTime.now());

        Map<Long, KetQuaGiaDTO> ketQua = new HashMap<Long, KetQuaGiaDTO>();
        for (ChiTietSanPham matHang : danhSach) {
            ketQua.put(matHang.getMaChiTietSanPham(), tinhTuKhuyenMai(matHang, khuyenMai.get(matHang.getMaChiTietSanPham())));
        }
        return ketQua;
    }

    @Override
    public KetQuaGiaDTO tinhGia(ChiTietSanPham matHang) {
        List<ChiTietSanPham> mot = new ArrayList<ChiTietSanPham>();
        mot.add(matHang);
        return tinhGiaNhieuMatHang(mot).get(matHang.getMaChiTietSanPham());
    }

    /**
     * note code nhu nay nhe ban: giam % = giaGoc x (100 - %) / 100 (lam tron den dong); giam so tien = giaGoc - soTien (khong am).
     * Giai doan nay KHONG kiem tra / cong don gioi han so luong khuyen mai (soLuongGioiHan, soLuongDaBan) - tam tat cung voi gioi han phieu giam gia.
     */
    private KetQuaGiaDTO tinhTuKhuyenMai(ChiTietSanPham matHang, ChiTietChuongTrinhKhuyenMai khuyenMai) {
        BigDecimal giaGoc = matHang.getGiaGoc();
        if (khuyenMai == null) {
            return new KetQuaGiaDTO(giaGoc, giaGoc, null);
        }
        BigDecimal giaBan;
        if ("PHAN_TRAM".equals(khuyenMai.getLoaiGiam())) {
            giaBan = giaGoc.multiply(new BigDecimal("100").subtract(khuyenMai.getGiaTriGiam()))
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
        } else {
            giaBan = giaGoc.subtract(khuyenMai.getGiaTriGiam()).setScale(0, RoundingMode.HALF_UP);
        }
        if (giaBan.signum() < 0) {
            giaBan = BigDecimal.ZERO;
        }
        if (giaBan.compareTo(giaGoc) > 0) {
            giaBan = giaGoc;
        }
        return new KetQuaGiaDTO(giaGoc, giaBan, khuyenMai.getMaChiTietChuongTrinhKhuyenMai());
    }
}
