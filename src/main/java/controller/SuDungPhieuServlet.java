package controller;

import dto.BoLoc;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.SuDungPhieuGiamGiaRepository;

import java.io.IOException;

/**
 * Luot su dung phieu giam gia: /wp-admin/phieu-giam-gia/su-dung  (module M4).
 * Bang lien quan: SuDungPhieuGiamGia (+ PhieuGiamGia, KhachHang). Loc DA_SU_DUNG / DA_HOAN.
 */
@WebServlet("/wp-admin/phieu-giam-gia/su-dung")
public class SuDungPhieuServlet extends BaseServlet {

    private final SuDungPhieuGiamGiaRepository repo = new SuDungPhieuGiamGiaRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/phieu-giam-gia/su-dung");
        hienThi(req, resp, "phieu-giam-gia/su-dung", "phieu-giam-gia", "Lượt sử dụng phiếu");
    }
}
