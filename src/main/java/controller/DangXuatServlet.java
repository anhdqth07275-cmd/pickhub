package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Dang xuat: huy session roi ve trang dang nhap.
 * Duoc goi o 2 noi: nut "Dang xuat" o sidebar, va doan script trong header.jspf khi nguoi dung reload trang (them ?lyDo=tai-lai).
 */
@WebServlet("/wp-admin/dang-xuat")
public class DangXuatServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();      // note code nhu nay nhe ban: invalidate() xoa toan bo du lieu session (taiKhoan, flash...)

        // note code nhu nay nhe ban: chi nhan dung 1 gia tri "tai-lai", con lai deu coi la dang xuat binh thuong
        String thongBao = "tai-lai".equals(req.getParameter("lyDo")) ? "tai-lai" : "dang-xuat";
        resp.sendRedirect(req.getContextPath() + "/wp-admin/dang-nhap?thongBao=" + thongBao);
    }
}
