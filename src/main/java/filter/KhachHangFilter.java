package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Bat buoc khach hang dang nhap de dung gio hang va dat hang.
 * Chua dang nhap: trang thuong -> chuyen toi /dang-nhap (nho trang dang muon vao), API (AJAX) -> tra 401 dang JSON.
 * Chan luon truy cap thang vao /trang-chu-khach/*.jsp (JSP chi mo duoc qua servlet).
 */
@WebFilter(urlPatterns = {"/gio-hang", "/dat-hang", "/dat-hang/*", "/api/gio-hang/*", "/trang-chu-khach/*"})
public class KhachHangFilter implements Filter {

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;
        String ctx = req.getContextPath();
        String duongDan = req.getRequestURI().substring(ctx.length());

        if (duongDan.startsWith("/trang-chu-khach")) {          // goi thang file JSP -> ve cua hang
            resp.sendRedirect(ctx + "/cua-hang");
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("khachHang") != null) {
            chain.doFilter(rq, rs);
            return;
        }

        // note code nhu nay nhe ban: AJAX khong redirect duoc -> tra 401 de JavaScript tu chuyen sang trang dang nhap
        if (duongDan.startsWith("/api/")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"ok\":false,\"dangNhap\":false,\"thongBao\":\"Vui lòng đăng nhập\"}");
            return;
        }
        resp.sendRedirect(ctx + "/dang-nhap?quayLai=" + duongDan);
    }
}
