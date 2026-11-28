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
        // note code nhu nay nhe ban: chi cho quay ve 1 trong 2 trang co dinh (danh sach trang), KHONG nhan duong dan tu nguoi dung -> tranh open redirect
        String quayLai = "dashboard".equals(docChuoi(req, "quayLai")) ? "/wp-admin" : "/wp-admin/ton-kho";
        try {
            if (hanhDong.equals("kiemKe")) {
                // note code nhu nay nhe ban: PhanQuyenUtil da mo quyen ghi ton-kho cho NHAN_VIEN (de nhap hang nhanh),
                // nen kiem ke (dat lai so ton) phai chan rieng o day: chi QUAN_LY tro len duoc kiem ke
                if (laNhanVien(req)) {
                    chuyenHuongLoi(req, resp, quayLai, "Nhân viên không có quyền kiểm kê tồn kho");
                    return;
                }
                // so ton nhap sai (chu, rong) -> -1 de bi chan o service
                service.capNhatTon(docLong(req, "id"), docSo(req, "soLuong", -1));
                chuyenHuong(req, resp, quayLai, "Đã cập nhật số lượng tồn");
            } else if (hanhDong.equals("nhapThem")) {
                // nhap them hang: ca NHAN_VIEN va QUAN_LY deu duoc
                service.nhapThem(docLong(req, "id"), docSo(req, "soLuong", 0));
                chuyenHuong(req, resp, quayLai, "Đã nhập thêm hàng vào kho");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, quayLai, e.getMessage());
        }
    }

    // note code nhu nay nhe ban: kiem tra nguoi dang dang nhap co phai NHAN_VIEN khong (vai tro luu trong session luc dang nhap)
    private boolean laNhanVien(HttpServletRequest req) {
        return taiKhoan(req) != null && "NHAN_VIEN".equals(taiKhoan(req).getVaiTro());
    }
}