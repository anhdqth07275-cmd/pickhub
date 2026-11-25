package controller;

import dto.BoLoc;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import exception.NghiepVuException;
import repository.PhieuGiamGiaRepository;
import repository.SuDungPhieuGiamGiaRepository;
import service.PhieuGiamGiaService;

import java.io.IOException;

/**
 * Luot su dung phieu giam gia: /wp-admin/phieu-giam-gia/su-dung  (module M4).
 * Bang lien quan: SuDungPhieuGiamGia (+ PhieuGiamGia, KhachHang). Loc DA_SU_DUNG / DA_HOAN.
 * Them ?maPhieu=... : chi xem 1 phieu + bang "moi khach da dung may lan / gioi han moi nguoi".
 */
@WebServlet("/wp-admin/phieu-giam-gia/su-dung")
public class SuDungPhieuServlet extends BaseServlet {

    private final SuDungPhieuGiamGiaRepository repo = new SuDungPhieuGiamGiaRepository();
    private final PhieuGiamGiaRepository phieuRepo = new PhieuGiamGiaRepository();
    private final PhieuGiamGiaService phieuService = new PhieuGiamGiaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);

        // note code nhu nay nhe ban: loc theo 1 phieu. maPhieu da doc thanh so Long nen noi thang vao dieu kien HQL la an toan (khong the chen chuoi)
        Long maPhieu = docLong(req, "maPhieu");
        if (maPhieu != null) {
            loc.dieuKienThem("x.phieuGiamGia.maPhieuGiamGia = " + maPhieu);
            try {
                req.setAttribute("phieuDangXem", phieuRepo.timTheoId(maPhieu));
                req.setAttribute("thongKeKhach", phieuService.thongKeTheoKhach(maPhieu));
            } catch (NghiepVuException e) {
                req.setAttribute("flashNoiDung", e.getMessage());
                req.setAttribute("flashLoai", "loi");
            }
        }
        req.setAttribute("danhSachPhieu", phieuRepo.layTatCa());     // do vao o chon phieu o dau trang
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/phieu-giam-gia/su-dung");
        hienThi(req, resp, "phieu-giam-gia/su-dung", "phieu-giam-gia", "Lượt sử dụng phiếu");
    }
}
