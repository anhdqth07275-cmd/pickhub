package filter;

import dto.TaiKhoanDangNhap;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Bao ve khu vuc /wp-admin/*: chua dang nhap -> ve trang dang nhap; sai vai tro -> 403.
 * Chan luon truy cap truc tiep vao /trang-chu-backend/*.jsp (JSP chi duoc mo thong qua servlet).
 * Ma tran quyen theo tab "Phan quyen" trong tai lieu module.
 */
@WebFilter(urlPatterns = {"/wp-admin/*", "/trang-chu-backend/*"})
public class WpAdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;
        String ctx = req.getContextPath();
        String duongDan = req.getRequestURI().substring(ctx.length());

        // note code nhu nay nhe ban: goi thang vao file JSP (khong qua servlet) -> day ve dashboard
        if (duongDan.startsWith("/trang-chu-backend")) {
            resp.sendRedirect(ctx + "/wp-admin");
            return;
        }

        if (duongDan.length() > 1 && duongDan.endsWith("/")) duongDan = duongDan.substring(0, duongDan.length() - 1);

        // trang dang nhap thi cho di qua
        if (duongDan.equals("/wp-admin/dang-nhap")) {
            chain.doFilter(rq, rs);
            return;
        }

        HttpSession session = req.getSession(false);
        TaiKhoanDangNhap tk = session == null ? null : (TaiKhoanDangNhap) session.getAttribute("taiKhoan");
        if (tk == null) {
            resp.sendRedirect(ctx + "/wp-admin/dang-nhap");
            return;
        }

        if (!coQuyen(tk.getVaiTro(), duongDan, req.getMethod())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(rq, rs);
    }

    // note code nhu nay nhe ban: ma tran quyen rut gon
    private boolean coQuyen(String vaiTro, String duongDan, String phuongThuc) {
        if ("QUAN_TRI".equals(vaiTro)) return true;

        // khuyen mai, phieu giam gia, nhan vien: chi QUAN_LY tro len
        boolean khuVucQuanLy = duongDan.startsWith("/wp-admin/khuyen-mai")
                || duongDan.startsWith("/wp-admin/phieu-giam-gia")
                || duongDan.startsWith("/wp-admin/nhan-vien");
        if (khuVucQuanLy) return "QUAN_LY".equals(vaiTro);

        // NHAN_VIEN chi duoc xem, chi duoc ghi (POST) o don hang va tai khoan ca nhan
        if ("NHAN_VIEN".equals(vaiTro) && "POST".equalsIgnoreCase(phuongThuc)) {
            return duongDan.startsWith("/wp-admin/don-hang") || duongDan.startsWith("/wp-admin/tai-khoan");
        }
        return true;
    }
}
