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
import java.util.HashMap;
import java.util.Map;

/** Dang nhap nhan vien: GET /wp-admin/dang-nhap (hien form), POST (xu ly). */
@WebServlet("/wp-admin/dang-nhap")
public class DangNhapServlet extends BaseServlet {

    private static final int PHUT_HET_PHIEN = 30;   // khong thao tac 30 phut thi session het han (khop web.xml)

    // note code nhu nay nhe ban: cac thong bao hien o trang dang nhap, URL chi mang "ma" (?thongBao=tai-lai).
    // Chi tra ve noi dung co san trong map nay, KHONG in lai chuoi nguoi dung gui len => khong bi chen script (XSS)
    private static final Map<String, String> THONG_BAO = new HashMap<String, String>();
    static {
        THONG_BAO.put("dang-xuat", "Bạn đã đăng xuất.");
        THONG_BAO.put("tai-lai", "Phiên đăng nhập đã kết thúc vì bạn tải lại trang. Vui lòng đăng nhập lại.");
        THONG_BAO.put("doi-mat-khau", "Đặt lại mật khẩu thành công. Hãy đăng nhập bằng mật khẩu mới.");
    }

    private final XacThucService xacThucService = new XacThucService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (taiKhoan(req) != null) {               // da dang nhap roi thi vao thang dashboard
            resp.sendRedirect(req.getContextPath() + "/wp-admin");
            return;
        }
        req.setAttribute("thongBao", THONG_BAO.get(req.getParameter("thongBao")));   // ma la => null => khong hien gi
        req.getRequestDispatcher("/trang-chu-backend/dang-nhap.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = docChuoi(req, "email");
        try {
            // matKhau KHONG trim: khoang trang co the la 1 phan cua mat khau
            TaiKhoanDangNhap tk = xacThucService.dangNhap(email, req.getParameter("matKhau") == null ? "" : req.getParameter("matKhau"));
            khoiTaoSession(req, tk);
            resp.sendRedirect(req.getContextPath() + "/wp-admin");
        } catch (NghiepVuException e) {
            req.setAttribute("loi", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/trang-chu-backend/dang-nhap.jsp").forward(req, resp);
        }
    }

    /**
     * KHOI TAO SESSION sau khi dang nhap dung.
     * note code nhu nay nhe ban: huy session cu roi tao session MOI (id moi) => chong tan cong "session fixation"
     * (ke xau gan san 1 session id cho nan nhan, nan nhan dang nhap xong thi ke xau dung chung id do).
     */
    private void khoiTaoSession(HttpServletRequest req, TaiKhoanDangNhap tk) {
        HttpSession cu = req.getSession(false);
        if (cu != null) cu.invalidate();                          // 1. bo session cu (neu co)

        HttpSession session = req.getSession(true);               // 2. tao session moi
        session.setAttribute("taiKhoan", tk);                     // 3. luu nguoi dung (WpAdminFilter va BaseServlet.taiKhoan() doc cai nay)
        session.setMaxInactiveInterval(PHUT_HET_PHIEN * 60);      // 4. het han sau 30 phut khong thao tac
    }
}
