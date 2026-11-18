package com.pickhub.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Dang xuat: /dang-xuat */
@WebServlet("/dang-xuat")
public class DangXuatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws IOException {
        if (yeuCau.getSession(false) != null) {
            yeuCau.getSession(false).invalidate();
        }
        phanHoi.sendRedirect(yeuCau.getContextPath() + "/dang-nhap");
    }
}
