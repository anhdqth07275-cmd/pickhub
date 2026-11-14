package controller;

import dto.BoLoc;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ChuongTrinhKhuyenMaiRepository;

import java.io.IOException;

/**
 * Chuong trinh khuyen mai: /wp-admin/khuyen-mai  (module M4). Chi QUAN_LY, QUAN_TRI.
 * Bang lien quan: ChuongTrinhKhuyenMai.
 * TODO: form them / sua (ngayKetThuc phai sau ngayBatDau, ten <= 150, luu maNguoiTao), scheduler tu doi
 *       SAP_CHAY -> DANG_CHAY -> KET_THUC (dang ky trong listener.KhoiTaoListener).
 */
@WebServlet("/wp-admin/khuyen-mai")
public class KhuyenMaiServlet extends BaseServlet {

    private final ChuongTrinhKhuyenMaiRepository repo = new ChuongTrinhKhuyenMaiRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTim("tenChuongTrinh")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/khuyen-mai");
        hienThi(req, resp, "khuyen-mai/danh-sach", "khuyen-mai", "Chương trình khuyến mãi");
    }
}
