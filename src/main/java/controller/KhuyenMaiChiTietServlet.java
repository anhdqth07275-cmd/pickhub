package controller;

import entity.ChuongTrinhKhuyenMai;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ChiTietChuongTrinhKhuyenMaiRepository;
import repository.ChuongTrinhKhuyenMaiRepository;

import java.io.IOException;

/**
 * Chi tiet 1 chuong trinh khuyen mai: /wp-admin/khuyen-mai/chi-tiet?id=...
 * Bang lien quan: ChuongTrinhKhuyenMai, ChiTietChuongTrinhKhuyenMai (danh sach chi tiet san pham duoc giam).
 * TODO: them / bo bien the khoi chuong trinh (khong trung chuong trinh + bien the; PHAN_TRAM trong (0, 100]; SO_TIEN < gia goc).
 */
@WebServlet("/wp-admin/khuyen-mai/chi-tiet")
public class KhuyenMaiChiTietServlet extends BaseServlet {

    private final ChuongTrinhKhuyenMaiRepository ctkmRepo = new ChuongTrinhKhuyenMaiRepository();
    private final ChiTietChuongTrinhKhuyenMaiRepository chiTietRepo = new ChiTietChuongTrinhKhuyenMaiRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = docLong(req, "id");
        ChuongTrinhKhuyenMai ct = ctkmRepo.timTheoId(id);
        if (ct == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/khuyen-mai", "Không tìm thấy chương trình khuyến mãi");
            return;
        }
        req.setAttribute("ct", ct);
        req.setAttribute("dsChiTiet", chiTietRepo.timTheoChuongTrinh(id));
        hienThi(req, resp, "khuyen-mai/chi-tiet", "khuyen-mai", "Chi tiết khuyến mãi");
    }
}
