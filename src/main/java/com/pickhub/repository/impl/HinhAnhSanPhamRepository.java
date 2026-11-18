package com.pickhub.repository.impl;

import com.pickhub.entity.HinhAnhSanPham;
import com.pickhub.repository.IHinhAnhSanPhamRepository;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HinhAnhSanPhamRepository extends BaseRepository implements IHinhAnhSanPhamRepository {

    @Override
    public Map<Long, String> layAnhChinhTheoSanPham(List<Long> danhSachMaSanPham) {
        Map<Long, String> ketQua = new HashMap<Long, String>();
        if (danhSachMaSanPham == null || danhSachMaSanPham.isEmpty()) {
            return ketQua;
        }
        Session session = moSession();
        try {
            List<HinhAnhSanPham> ds = session.createQuery(
                    "from HinhAnhSanPham h where h.laAnhChinh = true and h.maSanPham in (:ids)", HinhAnhSanPham.class)
                    .setParameterList("ids", danhSachMaSanPham).list();
            for (HinhAnhSanPham h : ds) {
                ketQua.put(h.getMaSanPham(), h.getDuongDanAnh());
            }
            return ketQua;
        } finally {
            session.close();
        }
    }

    @Override
    public Map<Long, String> layAnhRiengTheoBienThe(List<Long> danhSachMaBienThe) {
        Map<Long, String> ketQua = new HashMap<Long, String>();
        if (danhSachMaBienThe == null || danhSachMaBienThe.isEmpty()) {
            return ketQua;
        }
        Session session = moSession();
        try {
            List<HinhAnhSanPham> ds = session.createQuery(
                    "from HinhAnhSanPham h where h.maChiTietSanPham in (:ids) order by h.thuTuSapXep", HinhAnhSanPham.class)
                    .setParameterList("ids", danhSachMaBienThe).list();
            for (HinhAnhSanPham h : ds) {
                if (!ketQua.containsKey(h.getMaChiTietSanPham())) {
                    ketQua.put(h.getMaChiTietSanPham(), h.getDuongDanAnh());
                }
            }
            return ketQua;
        } finally {
            session.close();
        }
    }
}
