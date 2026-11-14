package service;

import dto.BienTheBanDTO;
import entity.ChiTietSanPham;
import dto.KetQuaGiaDTO;
import org.hibernate.Session;
import util.HibernateConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Danh sach bien the dang ban cho trang /cua-hang (BAN DEMO de thu gio hang; M3 se lam storefront day du: loc, tim kiem, chi tiet).
 */
public class CuaHangService {

    private final KhuyenMaiService khuyenMaiService = new KhuyenMaiService();

    public List<BienTheBanDTO> layBienTheDangBan() {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            List<ChiTietSanPham> ds = s.createQuery("from ChiTietSanPham c where c.trangThai = 'DANG_BAN' and c.sanPham.dangHienThi = true "
                    + "and c.soLuongTon > 0 order by c.sanPham.maSanPham, c.maChiTietSanPham", ChiTietSanPham.class).list();
            List<BienTheBanDTO> kq = new ArrayList<BienTheBanDTO>();
            for (ChiTietSanPham c : ds) {
                KetQuaGiaDTO gia = khuyenMaiService.tinhGia(s, c, 1);
                BienTheBanDTO b = new BienTheBanDTO();
                b.setMaChiTietSanPham(c.getMaChiTietSanPham());
                b.setTenSanPham(c.getSanPham().getTenSanPham());
                b.setMaHang(c.getMaHang());
                b.setMoTa(GioHangService.moTaBienThe(c));
                b.setGiaGoc(gia.getGiaGoc());
                b.setGiaSauGiam(gia.getGiaSauGiam());
                b.setSoLuongTon(c.getSoLuongTon());
                kq.add(b);
            }
            return kq;
        }
    }
}
