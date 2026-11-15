package controller;

import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ChiTietSanPhamService;
import service.HinhAnhSanPhamService;
import service.SanPhamService;
import service.ThuocTinhService;

import java.io.IOException;

/**
 * Trang chi tiet 1 san pham (M3): /wp-admin/san-pham/chi-tiet?maSanPham=...
 * Hien thong tin san pham + cac BIEN THE + HINH ANH; them / sua / ngung ban / xoa bien the.
 */
@WebServlet("/wp-admin/san-pham/chi-tiet")
public class SanPhamChiTietServlet extends BaseServlet {

    private final SanPhamService sanPhamService = new SanPhamService();
    private final ChiTietSanPhamService service = new ChiTietSanPhamService();
    private final HinhAnhSanPhamService hinhAnhService = new HinhAnhSanPhamService();
    private final ThuocTinhService thuocTinhService = new ThuocTinhService();       // M2: dropdown thuoc tinh

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long maSanPham = docLong(req, "maSanPham");
        if (maSanPham == null || sanPhamService.layTheoId(maSanPham) == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("sanPham", sanPhamService.layTheoId(maSanPham));
        req.setAttribute("danhSachBienThe", service.layTheoSanPham(maSanPham));
        req.setAttribute("danhSachAnh", hinhAnhService.layTheoSanPham(maSanPham));
        req.setAttribute("bienTheDangSua", service.layTheoId(docLong(req, "suaBienThe")));   // null khi them moi
        req.setAttribute("danhSachMauSac", thuocTinhService.layDangHoatDong(ThuocTinhService.MAU_SAC));
        req.setAttribute("danhSachKhoiLuong", thuocTinhService.layDangHoatDong(ThuocTinhService.KHOI_LUONG));
        req.setAttribute("danhSachChatLieu", thuocTinhService.layDangHoatDong(ThuocTinhService.CHAT_LIEU));
        req.setAttribute("danhSachChuVi", thuocTinhService.layDangHoatDong(ThuocTinhService.CHU_VI));
        hienThi(req, resp, "san-pham/chi-tiet", "san-pham", "Chi tiết sản phẩm");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");
        Long maSanPham = docLong(req, "maSanPham");
        String quayLai = "/wp-admin/san-pham/chi-tiet?maSanPham=" + maSanPham;
        try {
            if (hanhDong.equals("luuBienThe")) {
                service.luu(docLong(req, "id"), maSanPham, docChuoi(req, "maHang"), docLong(req, "maMauSac"), docLong(req, "maKhoiLuong"),
                        docLong(req, "maChatLieu"), docLong(req, "maChuViTayCam"), docChuoi(req, "doDayMm"), docChuoi(req, "giaGoc"),
                        docChuoi(req, "soLuongTon"), docChuoi(req, "nguongCanhBaoTonKho"));
                chuyenHuong(req, resp, quayLai, "Lưu biến thể thành công");
            } else if (hanhDong.equals("doiTrangThaiBan")) {
                service.doiTrangThaiBan(docLong(req, "id"));
                chuyenHuong(req, resp, quayLai, "Đã đổi trạng thái bán của biến thể");
            } else if (hanhDong.equals("xoaBienThe")) {
                service.xoa(docLong(req, "id"));
                chuyenHuong(req, resp, quayLai, "Đã xóa biến thể");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, quayLai, e.getMessage());
        }
    }
}
