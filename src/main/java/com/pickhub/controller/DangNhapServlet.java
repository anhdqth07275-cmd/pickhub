package com.pickhub.controller;

import com.pickhub.entity.NhanVien;
import com.pickhub.service.IXacThucService;
import com.pickhub.service.impl.XacThucServiceImpl;
import com.pickhub.util.NghiepVuException;
import com.pickhub.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Dang nhap nhan vien ban hang: /dang-nhap */
@WebServlet("/dang-nhap")
public class DangNhapServlet extends HttpServlet {

    private final IXacThucService xacThucService = new XacThucServiceImpl();

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws ServletException, IOException {
        if (yeuCau.getSession(false) != null && yeuCau.getSession(false).getAttribute("nhanVien") != null) {
            phanHoi.sendRedirect(yeuCau.getContextPath() + "/pos");
            return;
        }
        yeuCau.getRequestDispatcher("/jsp/dang-nhap.jsp").forward(yeuCau, phanHoi);
    }

    @Override
    protected void doPost(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws ServletException, IOException {
        try {
            NhanVien nhanVien = xacThucService.dangNhap(ServletUtil.docChuoi(yeuCau, "email"), yeuCau.getParameter("matKhau"));
            // note code nhu nay nhe ban: huy session cu, tao session MOI sau khi dang nhap (chong tan cong chiem session)
            if (yeuCau.getSession(false) != null) {
                yeuCau.getSession(false).invalidate();
            }
            yeuCau.getSession(true).setAttribute("nhanVien", nhanVien);
            phanHoi.sendRedirect(yeuCau.getContextPath() + "/pos");
        } catch (NghiepVuException loi) {
            yeuCau.setAttribute("loi", loi.getMessage());
            yeuCau.setAttribute("email", ServletUtil.docChuoi(yeuCau, "email"));
            yeuCau.getRequestDispatcher("/jsp/dang-nhap.jsp").forward(yeuCau, phanHoi);
        }
    }
}
