package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CuaHangService;

import java.io.IOException;

/**
 * Trang cua hang DEMO: /cua-hang - liet ke cac bien the dang ban + nut "Them vao gio" (AJAX).
 * TODO (M3): thay bang storefront day du (trang chu, danh sach, loc, tim kiem, chi tiet san pham).
 */
@WebServlet("/cua-hang")
public class CuaHangServlet extends BaseServlet {

    private final CuaHangService cuaHangService = new CuaHangService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dsBienThe", cuaHangService.layBienTheDangBan());
        hienThiKhach(req, resp, "cua-hang", "cua-hang", "Cửa hàng");
    }
}
