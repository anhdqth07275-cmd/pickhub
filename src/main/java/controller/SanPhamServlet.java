package controller;

import dto.SanPhamLocDTO;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.DanhMucService;
import service.SanPhamService;
import service.ThuocTinhService;
import service.ThuongHieuService;
import util.DocSoUtil;

import java.io.IOException;

/**
 * Quan ly san pham (M3): /wp-admin/san-pham
 * GET: danh sach (tim kiem + loc + sap xep + phan trang) hoac form them / sua.
 * POST: luu, doiHienThi, ngungKinhDoanh, xoa.
 */
@WebServlet("/wp-admin/san-pham")
public class SanPhamServlet extends BaseServlet {

    private final SanPhamService service = new SanPhamService();
    private final DanhMucService danhMucService = new DanhMucService();           // M2: du lieu dropdown
    private final ThuongHieuService thuongHieuService = new ThuongHieuService();  // M2
    private final ThuocTinhService thuocTinhService = new ThuocTinhService();     // M2

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");

        // ---- form them / sua ----
        if (hanhDong.equals("them") || hanhDong.equals("sua")) {
            req.setAttribute("sanPham", service.layTheoId(docLong(req, "id")));      // null khi them moi
            req.setAttribute("danhSachDanhMuc", danhMucService.layDanhMucDangHoatDong());
            req.setAttribute("danhSachThuongHieu", thuongHieuService.layDangHoatDong());
            hienThi(req, resp, "san-pham/form", "san-pham", hanhDong.equals("them") ? "Thêm sản phẩm" : "Sửa sản phẩm");
            return;
        }

        // ---- danh sach: doc cac dieu kien loc tu URL ----
        SanPhamLocDTO loc = new SanPhamLocDTO();
        try {
            loc.setTuKhoa(docChuoi(req, "tuKhoa"));
            loc.setMaDanhMuc(docLong(req, "maDanhMuc"));
            loc.setMaThuongHieu(docLong(req, "maThuongHieu"));
            loc.setMaMauSac(docLong(req, "maMauSac"));
            loc.setMaKhoiLuong(docLong(req, "maKhoiLuong"));
            loc.setGiaTu(DocSoUtil.soThapPhan(docChuoi(req, "giaTu"), "Giá từ"));
            loc.setGiaDen(DocSoUtil.soThapPhan(docChuoi(req, "giaDen"), "Giá đến"));
            String ht = docChuoi(req, "dangHienThi");
            loc.setDangHienThi(ht.equals("1") ? Boolean.TRUE : ht.equals("0") ? Boolean.FALSE : null);
            loc.setTinhTrangTon(docChuoi(req, "tinhTrangTon"));
            if (!docChuoi(req, "sapXep").isEmpty()) loc.setSapXep(docChuoi(req, "sapXep"));
            loc.setTrang(docSo(req, "trang", 1));
        } catch (NghiepVuException e) {
            // nhap sai o gia thi bao loi va bo qua bo loc gia
            req.setAttribute("flashNoiDung", e.getMessage());
            req.setAttribute("flashLoai", "loi");
            loc.setGiaTu(null);
            loc.setGiaDen(null);
        }
        req.setAttribute("loc", loc);
        req.setAttribute("ketQua", service.timKiem(loc));
        req.setAttribute("danhSachDanhMuc", danhMucService.layDanhMucDangHoatDong());
        req.setAttribute("danhSachThuongHieu", thuongHieuService.layDangHoatDong());
        req.setAttribute("danhSachMauSac", thuocTinhService.layDangHoatDong(ThuocTinhService.MAU_SAC));
        req.setAttribute("danhSachKhoiLuong", thuocTinhService.layDangHoatDong(ThuocTinhService.KHOI_LUONG));
        hienThi(req, resp, "san-pham/danh-sach", "san-pham", "Sản phẩm");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");
        try {
            if (hanhDong.equals("luu")) {
                service.luu(docLong(req, "id"), docChuoi(req, "tenSanPham"), docLong(req, "maDanhMuc"), docLong(req, "maThuongHieu"),
                        docChuoi(req, "loaiSanPham"), docChuoi(req, "moTaNgan"), docChuoi(req, "moTa"),
                        "1".equals(req.getParameter("dangHienThi")), taiKhoan(req).getId());
                chuyenHuong(req, resp, "/wp-admin/san-pham", "Lưu sản phẩm thành công");
            } else if (hanhDong.equals("doiHienThi")) {
                service.doiHienThi(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/san-pham", "Đã đổi trạng thái hiển thị");
            } else if (hanhDong.equals("ngungKinhDoanh")) {
                service.ngungKinhDoanh(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/san-pham", "Đã ngừng kinh doanh sản phẩm và các biến thể");
            } else if (hanhDong.equals("xoa")) {
                service.xoa(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/san-pham", "Đã xóa sản phẩm");
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NghiepVuException e) {
            // note code nhu nay nhe ban: luu loi (de trong ten, trung ten...) thi HIEN LAI FORM kem du lieu vua nhap,
            // khong redirect ve danh sach vi nguoi dung se mat het nhung gi da go
            if (hanhDong.equals("luu")) {
                Long id = docLong(req, "id");
                req.setAttribute("flashNoiDung", e.getMessage());
                req.setAttribute("flashLoai", "loi");
                req.setAttribute("giuDuLieu", Boolean.TRUE);                  // bao JSP lay gia tri tu param thay vi tu sanPham
                req.setAttribute("sanPham", service.layTheoId(id));           // null khi dang them moi
                req.setAttribute("danhSachDanhMuc", danhMucService.layDanhMucDangHoatDong());
                req.setAttribute("danhSachThuongHieu", thuongHieuService.layDangHoatDong());
                hienThi(req, resp, "san-pham/form", "san-pham", id == null ? "Thêm sản phẩm" : "Sửa sản phẩm");
                return;
            }
            chuyenHuongLoi(req, resp, "/wp-admin/san-pham", e.getMessage());
        }
    }
}
