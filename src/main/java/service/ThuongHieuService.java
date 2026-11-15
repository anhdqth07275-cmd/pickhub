package service;

import dto.BoLoc;
import dto.PhanTrang;
import entity.ThuongHieu;
import exception.NghiepVuException;
import repository.ThuongHieuRepository;
import util.SlugUtil;

import java.util.List;

/**
 * Nghiep vu THUONG HIEU (M2). M3 goi layDangHoatDong() cho dropdown form san pham.
 */
public class ThuongHieuService {

    private final ThuongHieuRepository repo = new ThuongHieuRepository();

    public List<ThuongHieu> layDangHoatDong() {
        return repo.layDangHoatDong();
    }

    public PhanTrang<ThuongHieu> timKiem(String tuKhoa, String trangThai, int trang) {
        return repo.timKiem(new BoLoc().cotTim("tenThuongHieu").tuKhoa(tuKhoa).cotTrangThai("trangThai").trangThai(trangThai).trang(trang));
    }

    public ThuongHieu layTheoId(Long id) {
        return id == null ? null : repo.timTheoId(id);
    }

    public ThuongHieu luu(Long id, String ten, String duongDanLogo) {
        ten = ten == null ? "" : ten.trim();
        duongDanLogo = duongDanLogo == null ? "" : duongDanLogo.trim();
        if (ten.isEmpty()) throw new NghiepVuException("Tên thương hiệu không được để trống");
        if (ten.length() > 100) throw new NghiepVuException("Tên thương hiệu tối đa 100 ký tự");
        if (duongDanLogo.length() > 500) throw new NghiepVuException("Đường dẫn logo tối đa 500 ký tự");
        if (repo.daTonTai("tenThuongHieu", ten, id)) throw new NghiepVuException("Thương hiệu này đã tồn tại");

        // note code nhu nay nhe ban: duong dan (slug) tu sinh tu ten, vd "Franklin Sports" -> franklin-sports
        String slug = SlugUtil.taoSlug(ten);
        if (slug.isEmpty()) throw new NghiepVuException("Tên thương hiệu phải có chữ hoặc số");
        if (repo.daTonTai("duongDan", slug, id)) throw new NghiepVuException("Tên này tạo đường dẫn trùng với thương hiệu khác");

        ThuongHieu th = new ThuongHieu();
        if (id != null) {
            th = repo.timTheoId(id);
            if (th == null) throw new NghiepVuException("Không tìm thấy thương hiệu");
        } else {
            th.setTrangThai("HOAT_DONG");
        }
        th.setTenThuongHieu(ten);
        th.setDuongDan(slug);
        th.setDuongDanLogo(duongDanLogo.isEmpty() ? null : duongDanLogo);
        if (id == null) repo.luu(th); else repo.capNhat(th);
        return th;
    }

    public void doiTrangThai(Long id) {
        ThuongHieu th = repo.timTheoId(id);
        if (th == null) throw new NghiepVuException("Không tìm thấy thương hiệu");
        if ("HOAT_DONG".equals(th.getTrangThai())) {
            long sp = repo.demSanPhamDangHienThi(id);
            if (sp > 0) throw new NghiepVuException("Còn " + sp + " sản phẩm đang hiển thị thuộc thương hiệu này");
            th.setTrangThai("NGUNG");
        } else {
            th.setTrangThai("HOAT_DONG");
        }
        repo.capNhat(th);
    }
}
