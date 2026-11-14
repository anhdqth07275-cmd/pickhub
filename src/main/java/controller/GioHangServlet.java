package controller;

import dto.GioHangDTO;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.GioHangService;

import java.io.IOException;

/**
 * Gio hang: /gio-hang  (module M5, bat buoc dang nhap - KhachHangFilter).
 * GET : xem gio (tu lam moi gia khuyen mai + canh bao).
 * POST: hanhDong = cap-nhat | xoa-dong | xoa-gio | ap-phieu | bo-phieu
 * Bang lien quan: HoaDon (trangThai GIO_HANG), HoaDonChiTiet.
 */
@WebServlet("/gio-hang")
public class GioHangServlet extends BaseServlet {

    private final GioHangService gioHangService = new GioHangService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GioHangDTO gio = gioHangService.layGioHang(khachHang(req).getId());
        req.setAttribute("gio", gio);
        hienThiKhach(req, resp, "gio-hang", "gio-hang", "Giỏ hàng");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long maKhach = khachHang(req).getId();
        String hanhDong = docChuoi(req, "hanhDong");
        try {
            switch (hanhDong) {
                case "cap-nhat":
                    gioHangService.capNhatSoLuong(maKhach, docLong(req, "maDong"), docSo(req, "soLuong", 1));
                    chuyenHuong(req, resp, "/gio-hang", "Đã cập nhật giỏ hàng");
                    return;
                case "xoa-dong":
                    gioHangService.xoaDong(maKhach, docLong(req, "maDong"));
                    chuyenHuong(req, resp, "/gio-hang", "Đã xóa sản phẩm khỏi giỏ");
                    return;
                case "xoa-gio":
                    gioHangService.xoaGio(maKhach);
                    chuyenHuong(req, resp, "/gio-hang", "Đã xóa toàn bộ giỏ hàng");
                    return;
                case "ap-phieu":
                    gioHangService.apDungPhieu(maKhach, docChuoi(req, "maPhieu"));
                    chuyenHuong(req, resp, "/gio-hang", "Đã áp dụng mã giảm giá");
                    return;
                case "bo-phieu":
                    gioHangService.boPhieu(maKhach);
                    chuyenHuong(req, resp, "/gio-hang", "Đã bỏ mã giảm giá");
                    return;
                default:
                    chuyenHuongLoi(req, resp, "/gio-hang", "Hành động không hợp lệ");
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, "/gio-hang", e.getMessage());
        }
    }
}
