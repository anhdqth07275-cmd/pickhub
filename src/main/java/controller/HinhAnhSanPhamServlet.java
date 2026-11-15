package controller;

import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.HinhAnhSanPhamService;

import java.io.IOException;

/** Quan ly hinh anh san pham (M3), chi nhan POST tu trang chi tiet san pham: them, sua, chon anh chinh, xoa. */
@WebServlet("/wp-admin/hinh-anh")
public class HinhAnhSanPhamServlet extends BaseServlet {

    private final HinhAnhSanPhamService service = new HinhAnhSanPhamService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");
        String quayLai = "/wp-admin/san-pham/chi-tiet?maSanPham=" + docLong(req, "maSanPham");
        try {
            if (hanhDong.equals("them")) {
                service.them(docLong(req, "maSanPham"), docLong(req, "maChiTietSanPham"), docChuoi(req, "duongDanAnh"), "1".equals(req.getParameter("laAnhChinh")));
                chuyenHuong(req, resp, quayLai, "Đã thêm hình ảnh");
            } else if (hanhDong.equals("sua")) {
                service.sua(docLong(req, "id"), docChuoi(req, "duongDanAnh"), docChuoi(req, "thuTu").isEmpty() ? null : docSo(req, "thuTu", 0));
                chuyenHuong(req, resp, quayLai, "Đã cập nhật hình ảnh");
            } else if (hanhDong.equals("datAnhChinh")) {
                service.datAnhChinh(docLong(req, "id"));
                chuyenHuong(req, resp, quayLai, "Đã chọn ảnh đại diện");
            } else if (hanhDong.equals("xoa")) {
                service.xoa(docLong(req, "id"));
                chuyenHuong(req, resp, quayLai, "Đã xóa hình ảnh");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, quayLai, e.getMessage());
        }
    }
}
