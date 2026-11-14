package controller;

import dto.BoLoc;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ChiTietSanPhamRepository;

import java.io.IOException;

/**
 * Ton kho: /wp-admin/ton-kho  (module M3).
 * Bang lien quan: ChiTietSanPham. Loc theo trang thai, tick "chi hien sap het" de xem canh bao.
 * TODO: form dieu chinh ton kho (soLuongTon >= 0; ton = 0 thi tu chuyen HET_HANG).
 */
@WebServlet("/wp-admin/ton-kho")
public class TonKhoServlet extends BaseServlet {

    private final ChiTietSanPhamRepository chiTietRepo = new ChiTietSanPhamRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTim("maHang")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .sapXep("x.soLuongTon asc")                 // it hang nhat len dau
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);

        // note code nhu nay nhe ban: canh bao = ton <= nguong canh bao cua chinh bien the do
        if ("1".equals(docChuoi(req, "canhBao"))) loc.dieuKienThem("x.soLuongTon <= x.nguongCanhBaoTonKho");

        req.setAttribute("pt", chiTietRepo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/ton-kho");
        hienThi(req, resp, "san-pham/ton-kho", "ton-kho", "Tồn kho");
    }
}
