package controller;

import dto.GioHangDTO;
import dto.ThongTinGiaoHangDTO;
import entity.HoaDon;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.HoaDonChiTietRepository;
import service.DatHangService;
import service.GioHangService;

import java.io.IOException;

/**
 * Dat hang: /dat-hang (form giao hang + dat hang) va /dat-hang/thanh-cong (xac nhan don vua dat).  Module M5.
 * Thanh toan: mac dinh khi nhan hang (COD), mien phi van chuyen (DB chua co cot phi ship / phuong thuc thanh toan).
 */
@WebServlet({"/dat-hang", "/dat-hang/thanh-cong"})
public class DatHangServlet extends BaseServlet {

    private final GioHangService gioHangService = new GioHangService();
    private final DatHangService datHangService = new DatHangService();
    private final HoaDonChiTietRepository chiTietRepo = new HoaDonChiTietRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long maKhach = khachHang(req).getId();

        // ---- trang xac nhan sau khi dat ----
        if (req.getRequestURI().endsWith("/thanh-cong")) {
            HoaDon hd = datHangService.timDonCuaKhach(maKhach, docChuoi(req, "ma"));   // chi xem duoc don cua chinh minh
            if (hd == null) {
                chuyenHuongLoi(req, resp, "/cua-hang", "Không tìm thấy đơn hàng");
                return;
            }
            req.setAttribute("hd", hd);
            req.setAttribute("dongSanPham", chiTietRepo.timTheoHoaDon(hd.getMaHoaDon()));
            hienThiKhach(req, resp, "dat-hang-thanh-cong", "", "Đặt hàng thành công");
            return;
        }

        // ---- trang dat hang ----
        GioHangDTO gio = gioHangService.layGioHang(maKhach);
        if (gio.isRong() || gio.isCoLoi()) {
            chuyenHuongLoi(req, resp, "/gio-hang", gio.isRong() ? "Giỏ hàng đang trống" : "Vui lòng xử lý các sản phẩm lỗi trong giỏ trước khi đặt hàng");
            return;
        }
        req.setAttribute("gio", gio);
        req.setAttribute("tt", datHangService.layThongTinGoiY(maKhach));
        hienThiKhach(req, resp, "dat-hang", "gio-hang", "Đặt hàng");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long maKhach = khachHang(req).getId();
        ThongTinGiaoHangDTO tt = new ThongTinGiaoHangDTO();
        tt.setTenNguoiNhan(docChuoi(req, "tenNguoiNhan"));
        tt.setSoDienThoaiNguoiNhan(docChuoi(req, "soDienThoaiNguoiNhan"));
        tt.setTinhThanh(docChuoi(req, "tinhThanh"));
        tt.setQuanHuyen(docChuoi(req, "quanHuyen"));
        tt.setPhuongXa(docChuoi(req, "phuongXa"));
        tt.setSoNhaDuong(docChuoi(req, "soNhaDuong"));
        tt.setGhiChu(docChuoi(req, "ghiChu"));

        try {
            String maTraCuu = datHangService.datHang(maKhach, tt);
            resp.sendRedirect(req.getContextPath() + "/dat-hang/thanh-cong?ma=" + maTraCuu);   // Post-Redirect-Get: F5 khong dat lai
        } catch (NghiepVuException e) {
            // loi: hien lai form kem du lieu vua nhap
            req.setAttribute("loi", e.getMessage());
            req.setAttribute("gio", gioHangService.layGioHang(maKhach));
            req.setAttribute("tt", tt);
            hienThiKhach(req, resp, "dat-hang", "gio-hang", "Đặt hàng");
        }
    }
}
