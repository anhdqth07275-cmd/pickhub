package controller;

import exception.NghiepVuException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.GioHangService;

import java.io.IOException;

/**
 * API AJAX cho icon gio hang tren header va nut "Them vao gio":
 *   GET  /api/gio-hang/so-luong  -> {"soLuong": 3}
 *   POST /api/gio-hang/them      (maChiTietSanPham, soLuong) -> {"ok":true,"soLuong":4,"thongBao":"..."}
 * Chua dang nhap -> KhachHangFilter tra 401.
 */
@WebServlet({"/api/gio-hang/so-luong", "/api/gio-hang/them"})
public class ApiGioHangServlet extends BaseServlet {

    private final GioHangService gioHangService = new GioHangService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int soLuong = gioHangService.demSoLuong(khachHang(req).getId());
        tra(resp, "{\"ok\":true,\"soLuong\":" + soLuong + "}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long maKhach = khachHang(req).getId();
        try {
            int soLuong = docSo(req, "soLuong", 1);
            gioHangService.themSanPham(maKhach, docLong(req, "maChiTietSanPham"), soLuong);
            tra(resp, "{\"ok\":true,\"soLuong\":" + gioHangService.demSoLuong(maKhach) + ",\"thongBao\":\"Đã thêm vào giỏ hàng\"}");
        } catch (NghiepVuException e) {
            tra(resp, "{\"ok\":false,\"thongBao\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private void tra(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(json);
    }

    // note code nhu nay nhe ban: escape dau " va \ de chuoi JSON khong bi vo
    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
