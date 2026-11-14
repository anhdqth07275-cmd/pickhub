package controller;

import dto.TaiKhoanDangNhap;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.XacThucService;

import java.io.IOException;

/** Dang nhap nhan vien: GET /wp-admin/dang-nhap (hien form), POST (xu ly). */
@WebServlet("/wp-admin/dang-nhap")
public class DangNhapServlet extends BaseServlet {

    private final XacThucService xacThucService = new XacThucService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (taiKhoan(req) != null) {               // da dang nhap roi thi vao thang dashboard
            resp.sendRedirect(req.getContextPath() + "/wp-admin");
            return;
        }
        req.getRequestDispatcher("/trang-chu-backend/dang-nhap.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = docChuoi(req, "email");
        try {
            TaiKhoanDangNhap tk = xacThucService.dangNhap(email, req.getParameter("matKhau") == null ? "" : req.getParameter("matKhau"));

            // note code nhu nay nhe ban: doi session id sau khi dang nhap de chong tan cong session fixation
            HttpSession session = req.getSession(true);
            req.changeSessionId();
            session.setAttribute("taiKhoan", tk);
            resp.sendRedirect(req.getContextPath() + "/wp-admin");
        } catch (NghiepVuException e) {
            req.setAttribute("loi", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/trang-chu-backend/dang-nhap.jsp").forward(req, resp);
        }
    }
}
