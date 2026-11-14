package controller;

import dto.BoLoc;
import entity.SanPham;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.SanPhamRepository;

import java.io.IOException;

/**
 * Danh sach san pham: /wp-admin/san-pham  (module M3).
 * Bang lien quan: SanPham (+ DanhMuc, ThuongHieu de hien ten).
 * Da lam: tim kiem, loc theo loai, bat / tat hien thi.
 * TODO: form them / sua san pham (tenSanPham <= 200, duongDan khong trung, moTaNgan <= 500).
 */
@WebServlet("/wp-admin/san-pham")
public class SanPhamServlet extends BaseServlet {

    private final SanPhamRepository sanPhamRepo = new SanPhamRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTim("tenSanPham")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("loaiSanPham")          // o day "trangThai" tren form chinh la loai san pham: VOT / BONG / PHU_KIEN / TUI
                .trangThai(docChuoi(req, "loai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", sanPhamRepo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/san-pham");
        hienThi(req, resp, "san-pham/danh-sach", "san-pham", "Sản phẩm");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // note code nhu nay nhe ban: "ngung ban thay vi xoa" - chi an san pham khoi storefront, khong xoa cung (vi con hoa don tham chieu)
        if ("an-hien".equals(docChuoi(req, "hanhDong"))) {
            SanPham sp = sanPhamRepo.timTheoId(docLong(req, "id"));
            if (sp != null) {
                sp.setDangHienThi(!Boolean.TRUE.equals(sp.getDangHienThi()));
                sanPhamRepo.capNhat(sp);
                chuyenHuong(req, resp, "/wp-admin/san-pham", sp.getDangHienThi() ? "Đã hiện sản phẩm" : "Đã ẩn sản phẩm");
                return;
            }
        }
        chuyenHuongLoi(req, resp, "/wp-admin/san-pham", "Không tìm thấy sản phẩm");
    }
}
