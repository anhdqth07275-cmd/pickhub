package com.pickhub.repository.impl;

import com.pickhub.entity.ChiTietChuongTrinhKhuyenMai;
import com.pickhub.repository.IKhuyenMaiRepository;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhuyenMaiRepository extends BaseRepository implements IKhuyenMaiRepository {

    @Override
    public Map<Long, ChiTietChuongTrinhKhuyenMai> timKhuyenMaiDangChay(List<Long> danhSachMaBienThe, LocalDateTime bayGio) {
        Map<Long, ChiTietChuongTrinhKhuyenMai> ketQua = new HashMap<Long, ChiTietChuongTrinhKhuyenMai>();
        if (danhSachMaBienThe == null || danhSachMaBienThe.isEmpty()) {
            return ketQua;
        }
        Session session = moSession();
        try {
            List<ChiTietChuongTrinhKhuyenMai> ds = session.createQuery(
                    "select k from ChiTietChuongTrinhKhuyenMai k, ChuongTrinhKhuyenMai p "
                            + "where k.maChuongTrinhKhuyenMai = p.maChuongTrinhKhuyenMai "
                            + "and k.chiTietSanPham.maChiTietSanPham in (:ids) "
                            + "and p.trangThai = 'DANG_CHAY' and p.ngayBatDau <= :now and p.ngayKetThuc >= :now",
                    ChiTietChuongTrinhKhuyenMai.class)
                    .setParameterList("ids", danhSachMaBienThe).setParameter("now", bayGio).list();
            for (ChiTietChuongTrinhKhuyenMai k : ds) {
                Long ma = k.getChiTietSanPham().getMaChiTietSanPham();
                if (!ketQua.containsKey(ma)) {          // 1 bien the chi nam trong 1 chuong trinh dang chay (da chan o khi them)
                    ketQua.put(ma, k);
                }
            }
            return ketQua;
        } finally {
            session.close();
        }
    }
}
