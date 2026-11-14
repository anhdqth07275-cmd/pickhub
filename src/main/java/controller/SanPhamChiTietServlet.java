package controller;

import entity.SanPham;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ChiTietSanPhamRepository;
import repository.HinhAnhSanPhamRepository;
import repository.SanPhamRepository;

import java.io.IOException;

/**
 * Chi tiet 1 san pham: /wp-admin/san-pham/chi-tiet?id=...
 * Bang lien quan: SanPham, ChiTietSanPham (cac bien the mau x khoi luong x chat lieu x chu vi), HinhAnhSanPham.
 * TODO: them / sua bien the (maHang <= 50 khong trung, giaGoc >= 0, soLuongTon >= 0), upload anh (JPG/PNG/WEBP, dung 1 anh chinh).
 */
@WebServlet("/wp-admin/san-pham/chi-tiet")
public class SanPhamChiTietServlet extends BaseServlet {

    private final SanPhamRepository sanPhamRepo = new SanPhamRepository();
    private final ChiTietSanPhamRepository chiTietRepo = new ChiTietSanPhamRepository();
    private final HinhAnhSanPhamRepository hinhAnhRepo = new HinhAnhSanPhamRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = docLong(req, "id");
        SanPham sp = sanPhamRepo.timTheoId(id);
        if (sp == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/san-pham", "Không tìm thấy sản phẩm");
            return;
        }
        req.setAttribute("sp", sp);
        req.setAttribute("bienThe", chiTietRepo.timTheoSanPham(id));
        req.setAttribute("hinhAnh", hinhAnhRepo.timTheoSanPham(id));
        hienThi(req, resp, "san-pham/chi-tiet", "san-pham", "Chi tiết sản phẩm");
    }
}
