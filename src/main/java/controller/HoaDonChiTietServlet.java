package controller;

import dto.TaiKhoanDangNhap;
import entity.HoaDon;
import enums.TrangThaiHoaDon;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.HoaDonChiTietRepository;
import repository.HoaDonRepository;
import repository.LichSuTrangThaiHoaDonRepository;
import service.DonHangService;

import java.io.IOException;

/**
 * Chi tiet 1 don hang: /wp-admin/don-hang/chi-tiet?id=...
 * Bang lien quan: HoaDon, HoaDonChiTiet, LichSuTrangThaiHoaDon.
 * GET : xem thong tin + cac dong san pham + timeline trang thai.
 * POST: chuyen sang buoc tiep theo (xac nhan -> giao hang -> hoan thanh).
 */
@WebServlet("/wp-admin/don-hang/chi-tiet")
public class HoaDonChiTietServlet extends BaseServlet {

    private final HoaDonRepository hoaDonRepo = new HoaDonRepository();
    private final HoaDonChiTietRepository chiTietRepo = new HoaDonChiTietRepository();
    private final LichSuTrangThaiHoaDonRepository lichSuRepo = new LichSuTrangThaiHoaDonRepository();
    private final DonHangService donHangService = new DonHangService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = docLong(req, "id");
        HoaDon hd = hoaDonRepo.timTheoId(id);
        if (hd == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/don-hang", "Không tìm thấy đơn hàng");
            return;
        }
        req.setAttribute("hd", hd);
        req.setAttribute("dongSanPham", chiTietRepo.timTheoHoaDon(id));
        req.setAttribute("lichSu", lichSuRepo.timTheoHoaDon(id));

        // note code nhu nay nhe ban: tinh buoc ke tiep de JSP hien dung 1 nut (null = het luong, khong hien nut)
        TrangThaiHoaDon tt = TrangThaiHoaDon.tuChuoi(hd.getTrangThai());
        TrangThaiHoaDon tiep = tt == null ? null : tt.tiepTheo();
        req.setAttribute("buocTiepTheo", tiep == null ? null : tiep.name());
        hienThi(req, resp, "don-hang/chi-tiet", "don-hang", "Chi tiết đơn hàng");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = docLong(req, "id");
        String duongDan = "/wp-admin/don-hang/chi-tiet?id=" + id;
        TaiKhoanDangNhap tk = taiKhoan(req);
        try {
            donHangService.chuyenTrangThai(id, docChuoi(req, "trangThaiMoi"), tk.getId());
            chuyenHuong(req, resp, duongDan, "Đã cập nhật trạng thái đơn hàng");
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, duongDan, e.getMessage());
        }
    }
}
