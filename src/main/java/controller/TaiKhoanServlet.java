package controller;

import entity.NhanVien;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.NhanVienRepository;

import java.io.IOException;

/**
 * Tai khoan ca nhan cua nhan vien dang dang nhap.
 * TODO (M2): sua ho so (hoTen, SDT, ngaySinh), doi anh dai dien, doi mat khau (can nhap mat khau cu).
 */
@WebServlet("/wp-admin/tai-khoan")
public class TaiKhoanServlet extends BaseServlet {

    private final NhanVienRepository nhanVienRepo = new NhanVienRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        NhanVien nv = nhanVienRepo.timTheoId(taiKhoan(req).getId());   // lay lai tu DB de co du lieu moi nhat
        req.setAttribute("nv", nv);
        hienThi(req, resp, "tai-khoan", "tai-khoan", "Tài khoản");
    }
}
