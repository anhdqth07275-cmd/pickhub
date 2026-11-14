package controller;

import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ThuocTinhService;

import java.io.IOException;

/**
 * Thuoc tinh san pham: /wp-admin/thuoc-tinh?loai=mausac | chat-lieu | khoi-luong | chu-vi  (module M2).
 * 1 servlet cho 4 bang: MauSac, ChatLieu, KhoiLuong, ChuViTayCam (phan biet bang tham so "loai").
 *   GET                       -> danh sach (tim kiem + loc trang thai + phan trang)
 *   GET ?suaId=5              -> danh sach, dong co ma 5 hien o nhap de sua tai cho
 *   POST hanhDong=them | sua | doi-trang-thai
 * Toan bo validate nam trong ThuocTinhService.
 */
@WebServlet("/wp-admin/thuoc-tinh")
public class ThuocTinhServlet extends BaseServlet {

    private final ThuocTinhService service = new ThuocTinhService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loai = service.chuanHoaLoai(docChuoi(req, "loai"));
        req.setAttribute("loai", loai);
        req.setAttribute("tenThuocTinh", service.tenThuocTinh(loai));
        req.setAttribute("suaId", docLong(req, "suaId"));
        req.setAttribute("pt", service.timKiem(loai, docChuoi(req, "tuKhoa"), docChuoi(req, "trangThai"), docSo(req, "trang", 1), 10));
        req.setAttribute("duongDan", "/wp-admin/thuoc-tinh");
        hienThi(req, resp, "san-pham/thuoc-tinh", "thuoc-tinh", "Thuộc tính sản phẩm");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String loai = service.chuanHoaLoai(docChuoi(req, "loai"));
        String duongDan = "/wp-admin/thuoc-tinh?loai=" + loai;
        String hanhDong = docChuoi(req, "hanhDong");
        try {
            if ("them".equals(hanhDong)) {
                service.them(loai, docChuoi(req, "giaTri"));
                chuyenHuong(req, resp, duongDan, "Đã thêm " + service.tenThuocTinh(loai).toLowerCase());
            } else if ("sua".equals(hanhDong)) {
                service.sua(loai, docLong(req, "id"), docChuoi(req, "giaTri"));
                chuyenHuong(req, resp, duongDan, "Đã sửa " + service.tenThuocTinh(loai).toLowerCase());
            } else {
                service.doiTrangThai(loai, docLong(req, "id"));
                chuyenHuong(req, resp, duongDan, "Đã cập nhật trạng thái");
            }
        } catch (NghiepVuException e) {
            // note code nhu nay nhe ban: loi nghiep vu (trung, sai dinh dang...) -> bao do tren dau trang, khong luu
            chuyenHuongLoi(req, resp, duongDan, e.getMessage());
        }
    }
}
