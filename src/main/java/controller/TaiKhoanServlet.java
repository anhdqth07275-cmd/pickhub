package controller;

import dto.TaiKhoanDangNhap;
import entity.NhanVien;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TaiKhoanService;

import java.io.IOException;

/**
 * Tai khoan ca nhan cua nhan vien: /wp-admin/tai-khoan
 *   GET ?tab=ho-so    -> tab "Thong tin ca nhan" (mac dinh)
 *   GET ?tab=mat-khau -> tab "Doi mat khau"
 *   POST hanhDong=cap-nhat-ho-so | doi-mat-khau
 * 2 chuc nang tach thanh 2 tab rieng (moi tab 1 duong link) thay vi dat canh nhau nhu truoc.
 */
@WebServlet("/wp-admin/tai-khoan")
public class TaiKhoanServlet extends BaseServlet {

    private static final String TAB_HO_SO = "ho-so";
    private static final String TAB_MAT_KHAU = "mat-khau";

    private final TaiKhoanService service = new TaiKhoanService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // note code nhu nay nhe ban: chi nhan 2 gia tri tab hop le, nhap bay ba thi ve tab ho so (khong in lai chuoi nguoi dung gui len)
        String tab = TAB_MAT_KHAU.equals(docChuoi(req, "tab")) ? TAB_MAT_KHAU : TAB_HO_SO;
        req.setAttribute("nv", service.layTheoId(taiKhoan(req).getId()));
        req.setAttribute("tab", tab);
        hienThi(req, resp, "tai-khoan", "tai-khoan", "Tài khoản cá nhân");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long maNhanVien = taiKhoan(req).getId();       // luon lay nguoi dang dang nhap, KHONG nhan id tu form (tranh sua tai khoan nguoi khac)
        String hanhDong = docChuoi(req, "hanhDong");
        String tab = hanhDong.equals("doi-mat-khau") ? TAB_MAT_KHAU : TAB_HO_SO;      // loi thi quay lai DUNG tab vua thao tac
        String quayLai = "/wp-admin/tai-khoan?tab=" + tab;
        try {
            if (hanhDong.equals("cap-nhat-ho-so")) {
                NhanVien nv = service.capNhatHoSo(maNhanVien, docChuoi(req, "hoTen"), docChuoi(req, "soDienThoai"), docChuoi(req, "ngaySinh"));
                // note code nhu nay nhe ban: lam moi session de ten tren sidebar doi ngay, khong can dang nhap lai
                req.getSession().setAttribute("taiKhoan",
                        new TaiKhoanDangNhap(nv.getMaNhanVien(), nv.getHoTen(), nv.getThuDienTu(), nv.getVaiTro(), nv.getDuongDanAnhDaiDien()));
                chuyenHuong(req, resp, quayLai, "Đã cập nhật thông tin cá nhân");
            } else if (hanhDong.equals("doi-mat-khau")) {
                // matKhau lay thang tu req.getParameter (khong trim)
                service.doiMatKhau(maNhanVien, req.getParameter("matKhauCu"), req.getParameter("matKhauMoi"), req.getParameter("nhapLai"));
                chuyenHuong(req, resp, quayLai, "Đổi mật khẩu thành công");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, quayLai, e.getMessage());
        }
    }
}
