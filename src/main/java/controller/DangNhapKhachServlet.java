package controller;

import dto.KhachHangDangNhap;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.GioHangService;
import service.XacThucService;

import java.io.IOException;

/**
 * Dang nhap / dang xuat cua KHACH HANG: /dang-nhap, /dang-xuat  (ban toi gian, M2 se hoan thien dang ky, quen mat khau...).
 * Sau khi dang nhap quay lai trang truoc do (quayLai); neu truoc do bam "Them vao gio" (them=maChiTietSanPham) thi them san pham do luon.
 */
@WebServlet({"/dang-nhap", "/dang-xuat"})
public class DangNhapKhachServlet extends BaseServlet {

    private final XacThucService xacThucService = new XacThucService();
    private final GioHangService gioHangService = new GioHangService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().equals("/dang-xuat")) {
            HttpSession session = req.getSession(false);
            if (session != null) session.removeAttribute("khachHang");   // chi bo phien khach, khong dung vao phien nhan vien
            resp.sendRedirect(req.getContextPath() + "/cua-hang");
            return;
        }
        if (khachHang(req) != null) {
            resp.sendRedirect(req.getContextPath() + "/cua-hang");
            return;
        }
        req.setAttribute("quayLai", quayLaiAnToan(req));
        req.setAttribute("them", docChuoi(req, "them"));
        hienThiKhach(req, resp, "dang-nhap", "", "Đăng nhập");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = docChuoi(req, "email");
        String matKhau = req.getParameter("matKhau") == null ? "" : req.getParameter("matKhau");
        try {
            KhachHangDangNhap kh = xacThucService.dangNhapKhachHang(email, matKhau);
            HttpSession session = req.getSession(true);
            req.changeSessionId();                      // chong session fixation
            session.setAttribute("khachHang", kh);

            // note code nhu nay nhe ban: dang nhap xong van them san pham khach dang chon luc truoc (M5-07)
            Long them = docLong(req, "them");
            if (them != null) {
                try {
                    gioHangService.themSanPham(kh.getId(), them, 1);
                    guiFlashOk(req, "Đã thêm sản phẩm vào giỏ hàng");
                } catch (NghiepVuException e) {
                    guiFlashLoi(req, e.getMessage());
                }
            }
            resp.sendRedirect(req.getContextPath() + quayLaiAnToan(req));
        } catch (NghiepVuException e) {
            req.setAttribute("loi", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("quayLai", quayLaiAnToan(req));
            req.setAttribute("them", docChuoi(req, "them"));
            hienThiKhach(req, resp, "dang-nhap", "", "Đăng nhập");
        }
    }

    // note code nhu nay nhe ban: chi cho quay lai duong dan noi bo (bat dau bang "/", khong phai "//") de tranh bi lua chuyen sang web la
    private String quayLaiAnToan(HttpServletRequest req) {
        String q = docChuoi(req, "quayLai");
        if (q.matches("^/[A-Za-z0-9\\-_/.?=&]*$") && !q.startsWith("//")) return q;
        return "/cua-hang";
    }

    private void guiFlashOk(HttpServletRequest req, String noiDung) {
        req.getSession().setAttribute("flashLoai", "ok");
        req.getSession().setAttribute("flashNoiDung", noiDung);
    }

    private void guiFlashLoi(HttpServletRequest req, String noiDung) {
        req.getSession().setAttribute("flashLoai", "loi");
        req.getSession().setAttribute("flashNoiDung", noiDung);
    }
}
