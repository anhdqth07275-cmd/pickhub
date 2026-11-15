package controller;

import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TonKhoService;

import java.io.IOException;

/**
 * Quan ly ton kho (M3): /wp-admin/ton-kho
 * Xem ton theo tung bien the (loc het hang / sap het / con hang), kiem ke (dat lai so ton), nhap them hang.
 */
@WebServlet("/wp-admin/ton-kho")
public class TonKhoServlet extends BaseServlet {

    private final TonKhoService service = new TonKhoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("ketQua", service.timKiem(docChuoi(req, "tuKhoa"), docChuoi(req, "tinhTrang"), docSo(req, "trang", 1)));
        req.setAttribute("soSapHet", service.demSapHet());      // hien canh bao tren dau trang
        hienThi(req, resp, "san-pham/ton-kho", "ton-kho", "Tồn kho");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");
        try {
            if (hanhDong.equals("kiemKe")) {
                // so ton nhap sai (chu, rong) -> -1 de bi chan o service
                service.capNhatTon(docLong(req, "id"), docSo(req, "soLuong", -1));
                chuyenHuong(req, resp, "/wp-admin/ton-kho", "Đã cập nhật số lượng tồn");
            } else if (hanhDong.equals("nhapThem")) {
                service.nhapThem(docLong(req, "id"), docSo(req, "soLuong", 0));
                chuyenHuong(req, resp, "/wp-admin/ton-kho", "Đã nhập thêm hàng vào kho");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, "/wp-admin/ton-kho", e.getMessage());
        }
    }
}
