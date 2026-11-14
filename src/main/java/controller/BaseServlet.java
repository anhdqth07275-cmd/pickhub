package controller;

import dto.TaiKhoanDangNhap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Lop cha cua moi servlet trong /wp-admin: ham dung chung de forward JSP, thong bao flash, doc tham so an toan.
 */
public abstract class BaseServlet extends HttpServlet {

    // Thu muc chua JSP cua backend
    private static final String THU_MUC_VIEW = "/trang-chu-backend/";

    /**
     * Mo 1 trang JSP.
     * view   : duong dan JSP so voi /trang-chu-backend/, bo duoi .jsp (vd "san-pham/danh-sach")
     * menu   : khoa menu ben trai can to sang (vd "san-pham")
     * tieuDe : tieu de lon cua trang
     */
    protected void hienThi(HttpServletRequest req, HttpServletResponse resp, String view, String menu, String tieuDe)
            throws ServletException, IOException {
        req.setAttribute("menu", menu);
        req.setAttribute("tieuDe", tieuDe);

        // note code nhu nay nhe ban: thong bao "flash" luu trong session o lan POST, lay ra hien 1 lan roi xoa (tranh hien lai khi F5)
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("flashNoiDung") != null) {
            req.setAttribute("flashNoiDung", session.getAttribute("flashNoiDung"));
            req.setAttribute("flashLoai", session.getAttribute("flashLoai"));
            session.removeAttribute("flashNoiDung");
            session.removeAttribute("flashLoai");
        }
        req.getRequestDispatcher(THU_MUC_VIEW + view + ".jsp").forward(req, resp);
    }

    /** Sau khi POST thanh cong: luu thong bao roi redirect (mau Post-Redirect-Get). */
    protected void chuyenHuong(HttpServletRequest req, HttpServletResponse resp, String duongDan, String thongBao) throws IOException {
        guiFlash(req, "ok", thongBao);
        resp.sendRedirect(req.getContextPath() + duongDan);
    }

    /** Nhu tren nhung la thong bao loi (mau do). */
    protected void chuyenHuongLoi(HttpServletRequest req, HttpServletResponse resp, String duongDan, String thongBao) throws IOException {
        guiFlash(req, "loi", thongBao);
        resp.sendRedirect(req.getContextPath() + duongDan);
    }

    private void guiFlash(HttpServletRequest req, String loai, String noiDung) {
        if (noiDung == null) return;
        req.getSession().setAttribute("flashLoai", loai);
        req.getSession().setAttribute("flashNoiDung", noiDung);
    }

    // ---------- doc tham so an toan ----------

    protected String docChuoi(HttpServletRequest req, String ten) {
        String v = req.getParameter(ten);
        return v == null ? "" : v.trim();
    }

    protected int docSo(HttpServletRequest req, String ten, int macDinh) {
        try {
            return Integer.parseInt(docChuoi(req, ten));
        } catch (NumberFormatException e) {
            return macDinh;
        }
    }

    protected Long docLong(HttpServletRequest req, String ten) {
        try {
            return Long.valueOf(docChuoi(req, ten));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Nhan vien dang dang nhap (luu trong session luc dang nhap). */
    protected TaiKhoanDangNhap taiKhoan(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (TaiKhoanDangNhap) session.getAttribute("taiKhoan");
    }
}
