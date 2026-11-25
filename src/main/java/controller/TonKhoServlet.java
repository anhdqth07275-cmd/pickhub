package controller;

import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TonKhoService;
import util.DocSoUtil;

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
        // note code nhu nay nhe ban: ten tham so phai KHOP voi form o ton-kho.jsp va link o dashboard (?canhBao=1)
        boolean chiSapHet = "1".equals(docChuoi(req, "canhBao"));
        // LOI CU: servlet gui bien "ketQua" nhung JSP doc "pt" nen bang luon trong -> doi thanh "pt" + them "duongDan" cho phan trang
        req.setAttribute("pt", service.timKiem(docChuoi(req, "tuKhoa"), docChuoi(req, "trangThai"), chiSapHet, docSo(req, "trang", 1)));
        req.setAttribute("duongDan", "/wp-admin/ton-kho");
        req.setAttribute("soSapHet", service.demSapHet());      // hien canh bao tren dau trang
        hienThi(req, resp, "san-pham/ton-kho", "ton-kho", "Tồn kho");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");
        try {
            if (hanhDong.equals("kiemKe")) {
                // note code nhu nay nhe ban: kiem ke cho phep dat ton = 0 (kho het sach). docSo() coi so < 1 la khong hop le nen
                // khong dung duoc o day -> dung DocSoUtil.soNguyen (nhap chu thi bao loi, de trong thi tra ve null)
                Integer soLuong = DocSoUtil.soNguyen(docChuoi(req, "soLuong"), "Số lượng tồn");
                if (soLuong == null) throw new NghiepVuException("Vui lòng nhập số lượng tồn");
                service.capNhatTon(docLong(req, "id"), soLuong);       // so am -> service bao loi
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
