package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Cac chuc nang chua lam: Dat san va Ban hang tai quay -> hien trang "Sap ra mat".
 * Khi lam that: tach thanh servlet rieng va xoa dong mapping tuong ung o day.
 */
@WebServlet({"/wp-admin/dat-san", "/wp-admin/ban-hang-tai-quay"})
public class SapRaMatServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // note code nhu nay nhe ban: dua vao duong dan de biet dang bam vao chuc nang nao
        boolean laDatSan = req.getServletPath().endsWith("dat-san");
        req.setAttribute("tenChucNang", laDatSan ? "Đặt sân" : "Bán hàng tại quầy");
        req.setAttribute("moTaChucNang", laDatSan
                ? "Quản lý cơ sở sân, sân con, bảng giá, lịch đặt sân và kèo tìm bạn chơi."
                : "Bán hàng trực tiếp tại cửa hàng, in hóa đơn cho khách mua tại quầy.");
        hienThi(req, resp, "sap-ra-mat", laDatSan ? "dat-san" : "tai-quay", req.getAttribute("tenChucNang").toString());
    }
}
