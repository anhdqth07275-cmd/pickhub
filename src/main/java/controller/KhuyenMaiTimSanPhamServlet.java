package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.KhuyenMaiService;

import java.io.IOException;

/**
 * O "Tim ma hoac ten san pham..." trong popup khuyen mai: GET /wp-admin/khuyen-mai/tim-san-pham?tuKhoa=...
 * Chi QUAN_LY, QUAN_TRI (WpAdminFilter chan theo duong dan /wp-admin/khuyen-mai...).
 *
 * note code nhu nay nhe ban: tra ve cac DONG <tr> (doan HTML), khong phai ca trang va khong phai JSON.
 * Dong nay duoc ve bang CHUNG 1 file dong-san-pham.jspf voi luc mo popup -> chi 1 noi dinh nghia giao dien dong san pham.
 * khuyen-mai.js nhan doan HTML nay va them vao bang.
 */
@WebServlet("/wp-admin/khuyen-mai/tim-san-pham")
public class KhuyenMaiTimSanPhamServlet extends BaseServlet {

    private static final int SO_KET_QUA = 30;       // toi da 30 dong moi lan tim de nhe may khi du lieu lon

    private final KhuyenMaiService service = new KhuyenMaiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("dsDong", service.timSanPhamDeChon(docChuoi(req, "tuKhoa"), SO_KET_QUA, null));
        resp.setContentType("text/html; charset=UTF-8");
        req.getRequestDispatcher("/trang-chu-backend/khuyen-mai/ket-qua-tim.jsp").forward(req, resp);
    }
}
