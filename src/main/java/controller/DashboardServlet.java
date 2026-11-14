package controller;

import dto.TongQuanDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ThongKeService;

import java.io.IOException;

/**
 * Trang chu backend: /wp-admin  (the so, bieu do doanh thu, viec can xu ly, don gan day, tong quan 18 bang).
 */
@WebServlet({"/wp-admin", "/wp-admin/"})
public class DashboardServlet extends BaseServlet {

    private final ThongKeService thongKeService = new ThongKeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // note code nhu nay nhe ban: ?kieu=thang -> bieu do 6 thang, mac dinh la 7 ngay
        TongQuanDTO tq = thongKeService.layTongQuan(docChuoi(req, "kieu"));
        req.setAttribute("tq", tq);
        hienThi(req, resp, "dashboard", "dashboard", "Dashboard");
    }
}
