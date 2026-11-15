package controller;

import dto.LichSuLocDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.LichSuTrangThaiService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Tra cuu lich su trang thai don hang: /wp-admin/lich-su-trang-thai  (module M6).
 * Bang lien quan: LichSuTrangThaiHoaDon (+ HoaDon de lay ma tra cuu).
 * Loc theo ma tra cuu, trang thai moi, khoang ngay, nguoi thuc hien (nhan vien / khach-he thong).
 */
@WebServlet("/wp-admin/lich-su-trang-thai")
public class LichSuTrangThaiServlet extends BaseServlet {

    private final LichSuTrangThaiService lichSuService = new LichSuTrangThaiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LichSuLocDTO loc = new LichSuLocDTO();
        loc.tuKhoa = docChuoi(req, "tuKhoa");
        loc.denTrangThai = docChuoi(req, "trangThai");
        loc.nguoiThucHien = docChuoi(req, "nguoi");
        loc.tuNgay = docNgay(req, "tuNgay");
        loc.denNgay = docNgay(req, "denNgay");
        loc.trang = Math.max(docSo(req, "trang", 1), 1);

        // note code nhu nay nhe ban: nguoi dung chon nguoc (tu ngay > den ngay) thi doi cho 2 ngay thay vi bao loi
        if (loc.tuNgay != null && loc.denNgay != null && loc.tuNgay.isAfter(loc.denNgay)) {
            LocalDate tam = loc.tuNgay;
            loc.tuNgay = loc.denNgay;
            loc.denNgay = tam;
        }

        req.setAttribute("pt", lichSuService.timKiem(loc));
        // menu = "don-hang" de sidebar van to sang muc "Don hang" (khong can sua header.jspf)
        hienThi(req, resp, "don-hang/lich-su", "don-hang", "Lịch sử trạng thái đơn hàng");
    }

    // doc ngay dang yyyy-MM-dd tu o <input type="date">; de trong hoac sai dinh dang -> null (khong loc)
    private LocalDate docNgay(HttpServletRequest req, String ten) {
        try {
            return LocalDate.parse(docChuoi(req, ten));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
