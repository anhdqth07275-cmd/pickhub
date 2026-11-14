package controller;

import dto.BoLoc;
import entity.ThuongHieu;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ThuongHieuRepository;
import util.SlugUtil;

import java.io.IOException;

/**
 * Thuong hieu: /wp-admin/thuong-hieu  (module M1).
 * Bang lien quan: ThuongHieu.
 * ** DAY LA MODULE MAU DAY DU CRUD ** - cac module danh muc khac copy theo mau nay:
 *   GET                      -> danh sach (tim kiem + loc + phan trang)
 *   GET ?hanhDong=form[&id=] -> form them / sua
 *   POST hanhDong=luu        -> them hoac sua (co validate)
 *   POST hanhDong=doi-trang-thai -> ngung / kich hoat (khong xoa cung)
 */
@WebServlet("/wp-admin/thuong-hieu")
public class ThuongHieuServlet extends BaseServlet {

    private final ThuongHieuRepository repo = new ThuongHieuRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("form".equals(docChuoi(req, "hanhDong"))) {
            Long id = docLong(req, "id");
            ThuongHieu th = id == null ? new ThuongHieu() : repo.timTheoId(id);
            if (th == null) th = new ThuongHieu();
            req.setAttribute("th", th);
            hienThi(req, resp, "thuong-hieu/form", "thuong-hieu", id == null ? "Thêm thương hiệu" : "Sửa thương hiệu");
            return;
        }

        BoLoc loc = new BoLoc()
                .cotTim("tenThuongHieu")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/thuong-hieu");
        hienThi(req, resp, "thuong-hieu/danh-sach", "thuong-hieu", "Thương hiệu");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");

        if ("doi-trang-thai".equals(hanhDong)) {
            // note code nhu nay nhe ban: HOAT_DONG <-> NGUNG, khong xoa cung de tranh loi khoa ngoai (SanPham dang tham chieu)
            ThuongHieu th = repo.timTheoId(docLong(req, "id"));
            if (th != null) {
                th.setTrangThai("HOAT_DONG".equals(th.getTrangThai()) ? "NGUNG" : "HOAT_DONG");
                repo.capNhat(th);
            }
            chuyenHuong(req, resp, "/wp-admin/thuong-hieu", "Đã cập nhật trạng thái thương hiệu");
            return;
        }

        // ---- luu (them / sua) ----
        Long id = docLong(req, "id");
        ThuongHieu th = id == null ? new ThuongHieu() : repo.timTheoId(id);
        if (th == null) th = new ThuongHieu();
        th.setTenThuongHieu(docChuoi(req, "tenThuongHieu"));
        th.setDuongDanLogo(docChuoi(req, "duongDanLogo").isEmpty() ? null : docChuoi(req, "duongDanLogo"));
        try {
            kiemTra(th, id);
            if (id == null) {
                th.setTrangThai("HOAT_DONG");
                repo.luu(th);
            } else {
                repo.capNhat(th);
            }
            chuyenHuong(req, resp, "/wp-admin/thuong-hieu", "Đã lưu thương hiệu");
        } catch (NghiepVuException e) {
            // loi validate: hien lai form kem du lieu vua nhap
            req.setAttribute("th", th);
            req.setAttribute("loi", e.getMessage());
            hienThi(req, resp, "thuong-hieu/form", "thuong-hieu", id == null ? "Thêm thương hiệu" : "Sửa thương hiệu");
        }
    }

    // note code nhu nay nhe ban: validate theo rang buoc SQL - ten bat buoc <= 100 va khong trung, duongDan <= 120 khong trung, logo <= 500
    private void kiemTra(ThuongHieu th, Long id) {
        if (th.getTenThuongHieu().isEmpty()) throw new NghiepVuException("Tên thương hiệu không được để trống");
        if (th.getTenThuongHieu().length() > 100) throw new NghiepVuException("Tên thương hiệu tối đa 100 ký tự");
        if (th.getDuongDanLogo() != null && th.getDuongDanLogo().length() > 500) throw new NghiepVuException("Đường dẫn logo tối đa 500 ký tự");

        th.setDuongDan(SlugUtil.taoSlug(th.getTenThuongHieu()));     // duongDan tu sinh tu ten
        if (th.getDuongDan().isEmpty()) throw new NghiepVuException("Tên thương hiệu không hợp lệ");
        if (th.getDuongDan().length() > 120) throw new NghiepVuException("Đường dẫn sinh ra quá dài (tối đa 120 ký tự)");

        if (repo.daTonTai("tenThuongHieu", th.getTenThuongHieu(), id)) throw new NghiepVuException("Tên thương hiệu đã tồn tại");
        if (repo.daTonTai("duongDan", th.getDuongDan(), id)) throw new NghiepVuException("Đường dẫn thương hiệu đã tồn tại");
    }
}
