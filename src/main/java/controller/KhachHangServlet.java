package controller;

import dto.BoLoc;
import entity.KhachHang;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.KhachHangRepository;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Quan ly khach hang: /wp-admin/khach-hang  (module M2).
 * Bang lien quan: KhachHang. Da lam: tim kiem, loc trang thai, khoa / mo khoa co ly do.
 */
@WebServlet("/wp-admin/khach-hang")
public class KhachHangServlet extends BaseServlet {

    private final KhachHangRepository repo = new KhachHangRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTim("hoTen,thuDienTu,soDienThoai")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/khach-hang");
        hienThi(req, resp, "khach-hang", "khach-hang", "Quản lý khách hàng");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        KhachHang kh = repo.timTheoId(docLong(req, "id"));
        String hanhDong = docChuoi(req, "hanhDong");
        if (kh == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/khach-hang", "Không tìm thấy khách hàng");
            return;
        }

        if ("khoa".equals(hanhDong)) {
            // note code nhu nay nhe ban: khoa phai co ly do + ngayKhoa (cot lyDoKhoa <= 255 ky tu)
            String lyDo = docChuoi(req, "lyDo").isEmpty() ? "Quản trị viên khóa tài khoản" : docChuoi(req, "lyDo");
            kh.setTrangThai("KHOA");
            kh.setLyDoKhoa(lyDo.length() > 255 ? lyDo.substring(0, 255) : lyDo);
            kh.setNgayKhoa(LocalDateTime.now());
            repo.capNhat(kh);
            chuyenHuong(req, resp, "/wp-admin/khach-hang", "Đã khóa tài khoản " + kh.getHoTen());
        } else if ("mo-khoa".equals(hanhDong)) {
            // note code nhu nay nhe ban: mo khoa thi reset bo dem sai; neu chua tung xac thuc email thi ve CHUA_XAC_THUC
            kh.setTrangThai(kh.getNgayXacThucThuDienTu() != null ? "HOAT_DONG" : "CHUA_XAC_THUC");
            kh.setSoLanDangNhapSai(0);
            kh.setLyDoKhoa(null);
            kh.setNgayKhoa(null);
            repo.capNhat(kh);
            chuyenHuong(req, resp, "/wp-admin/khach-hang", "Đã mở khóa tài khoản " + kh.getHoTen());
        } else {
            chuyenHuongLoi(req, resp, "/wp-admin/khach-hang", "Hành động không hợp lệ");
        }
    }
}
