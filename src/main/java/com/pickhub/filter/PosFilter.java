package com.pickhub.filter;

import com.pickhub.util.JsonUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Bao ve toan bo khu vuc ban hang /pos/*: chua dang nhap thi ve trang dang nhap
 * (rieng cac API /pos/api/* tra loi HTTP 401 dang JSON de man hinh ban hang tu bao het phien).
 */
@WebFilter(urlPatterns = {"/pos", "/pos/*"})
public class PosFilter implements Filter {

    @Override
    public void doFilter(ServletRequest yeuCau, ServletResponse phanHoi, FilterChain chuoi) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) yeuCau;
        HttpServletResponse tra = (HttpServletResponse) phanHoi;
        HttpSession phien = http.getSession(false);
        boolean daDangNhap = phien != null && phien.getAttribute("nhanVien") != null;
        if (daDangNhap) {
            chuoi.doFilter(yeuCau, phanHoi);
            return;
        }
        if (http.getRequestURI().contains("/pos/api/")) {
            tra.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            tra.setContentType("application/json;charset=UTF-8");
            tra.getWriter().write(JsonUtil.loi("Hết phiên đăng nhập, vui lòng đăng nhập lại"));
            return;
        }
        tra.sendRedirect(http.getContextPath() + "/dang-nhap");
    }
}
