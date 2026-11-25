package filter;

import dto.TaiKhoanDangNhap;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.PhanQuyenUtil;
import com.pickhub.repository.impl.NhanVienRepository;   // repository cua POS, dung de nap session "nhanVien" cho POS

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Bao ve khu vuc /wp-admin/*: chua dang nhap -> ve trang dang nhap;
 * vai tro NHAN_VIEN -> day sang man hinh ban hang POS (/pos); trang khong du quyen -> 403.
 * Chan luon truy cap truc tiep vao /trang-chu-backend/*.jsp (JSP chi duoc mo thong qua servlet).
 * Ma tran quyen nam o util.PhanQuyenUtil (filter va menu cung dung chung 1 cho).
 */
@WebFilter(urlPatterns = {"/wp-admin/*", "/trang-chu-backend/*"})
public class WpAdminFilter implements Filter {

    // note code: chi trang dang nhap la cong khai; dat lai mat khau nhan vien do quan tri vien xu ly.
    private static final Set<String> TRANG_CONG_KHAI = new HashSet<String>(Arrays.asList(
            "/wp-admin/dang-nhap"
    ));

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;
        String ctx = req.getContextPath();

        // note code nhu nay nhe ban: dung getServletPath() + getPathInfo() (da giai ma %xx va chuan hoa ../ ;jsessionid)
        // KHONG dung getRequestURI() vi no la chuoi tho: /wp-admin/%6Bhuyen-mai se lot qua kiem tra startsWith("/wp-admin/khuyen-mai")
        String duongDan = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());
        if (duongDan.length() > 1 && duongDan.endsWith("/")) duongDan = duongDan.substring(0, duongDan.length() - 1);

        // goi thang vao file JSP (khong qua servlet) -> day ve dashboard
        if (duongDan.startsWith("/trang-chu-backend")) {
            resp.sendRedirect(ctx + "/wp-admin");
            return;
        }

        // note code nhu nay nhe ban: cam trinh duyet luu cache trang quan tri.
        // Neu khong, sau khi dang xuat bam nut Back van thay lai trang cu (du lieu nhay cam)
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        // trang cong khai thi cho di qua
        if (TRANG_CONG_KHAI.contains(duongDan)) {
            chain.doFilter(rq, rs);
            return;
        }

        // buoc 1: da dang nhap chua? (khong co session hoac session khong co "taiKhoan" => chua)
        HttpSession session = req.getSession(false);
        TaiKhoanDangNhap tk = session == null ? null : (TaiKhoanDangNhap) session.getAttribute("taiKhoan");
        if (tk == null) {
            resp.sendRedirect(ctx + "/wp-admin/dang-nhap");
            return;
        }

        // dang xuat: ai da dang nhap cung duoc bam, khong xet vai tro (de nhan vien thoat duoc tai khoan)
        if (duongDan.equals("/wp-admin/dang-xuat")) {
            chain.doFilter(rq, rs);
            return;
        }

        // buoc 2 (MOI): chi QUAN_LY va QUAN_TRI moi duoc vao /wp-admin. Vai tro khac -> khong bao loi 403 ma day thang sang POS
        if (!PhanQuyenUtil.duocVaoAdmin(tk.getVaiTro())) {
            chuyenSangPos(req, resp, session, tk);
            return;
        }

        // buoc 3: co quyen vao trang nay khong? (dung vai tro luu luc dang nhap)
        if (!PhanQuyenUtil.coQuyen(tk.getVaiTro(), duongDan, req.getMethod())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(rq, rs);
    }

    /**
     * Day nhan vien sang POS.
     * note code nhu nay nhe ban: trang admin luu nguoi dung o session "taiKhoan", con POS (PosFilter) doc session "nhanVien".
     * Neu khong nap them "nhanVien" thi PosFilter se bat nhan vien dang nhap lai lan nua.
     */
    private void chuyenSangPos(HttpServletRequest req, HttpServletResponse resp, HttpSession session, TaiKhoanDangNhap tk) throws IOException {
        if (session.getAttribute("nhanVien") == null) {
            session.setAttribute("nhanVien", new NhanVienRepository().timTheoEmail(tk.getEmail()));
        }
        resp.sendRedirect(req.getContextPath() + "/pos");
    }
}
