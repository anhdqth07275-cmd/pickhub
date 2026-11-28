package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TimKiemService;

import java.io.IOException;

/**
 * Tim kiem toan cuc: /wp-admin/tim-kiem?q=...  (thanh tim kiem o dau moi trang, phim tat Ctrl + K).
 * Chi doc du lieu (GET) nen NHAN_VIEN cung dung duoc.
 */
@WebServlet("/wp-admin/tim-kiem")
public class TimKiemServlet extends BaseServlet {

    private final TimKiemService service = new TimKiemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = docChuoi(req, "q");
        // note code nhu nay nhe ban: nguoi dung hay go "#HD2611..." -> bo dau # de khop maTraCuu trong DB
        if (q.startsWith("#")) q = q.substring(1).trim();
        req.setAttribute("q", q);

        // note code nhu nay nhe ban: it nhat 2 ky tu moi tim (tranh quet ca bang chi voi 1 chu)
        if (q.length() >= 2) {
            req.setAttribute("donHang", service.timDonHang(q));
            req.setAttribute("khachHang", service.timKhachHang(q));
            req.setAttribute("sanPham", service.timSanPham(q));
        }
        hienThi(req, resp, "tim-kiem", "tim-kiem", "Kết quả tìm kiếm");
    }
}