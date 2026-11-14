package controller;

import dto.BoLoc;
import dto.TaiKhoanDangNhap;
import entity.NhanVien;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.NhanVienRepository;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Quan ly nhan vien: /wp-admin/nhan-vien  (module M2). Chi QUAN_LY, QUAN_TRI vao duoc (WpAdminFilter).
 * Bang lien quan: NhanVien. Da lam: tim kiem, loc, khoa / mo khoa.
 * TODO: form tao nhan vien moi (luu maNguoiTao = nguoi dang dang nhap, bam mat khau bang MatKhauUtil.bam), doi vai tro, dat lai mat khau.
 */
@WebServlet("/wp-admin/nhan-vien")
public class NhanVienServlet extends BaseServlet {

    private final NhanVienRepository repo = new NhanVienRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTim("hoTen,thuDienTu,soDienThoai")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .sapXep("x.maNhanVien asc")
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/nhan-vien");
        hienThi(req, resp, "nhan-vien", "nhan-vien", "Quản lý nhân viên");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TaiKhoanDangNhap toi = taiKhoan(req);
        NhanVien nv = repo.timTheoId(docLong(req, "id"));
        String hanhDong = docChuoi(req, "hanhDong");
        String duongDan = "/wp-admin/nhan-vien";

        if (nv == null) {
            chuyenHuongLoi(req, resp, duongDan, "Không tìm thấy nhân viên");
            return;
        }
        // note code nhu nay nhe ban: khong cho tu khoa chinh minh (tranh mat quyen quan tri)
        if (nv.getMaNhanVien().equals(toi.getId())) {
            chuyenHuongLoi(req, resp, duongDan, "Bạn không thể tự khóa / mở khóa chính mình");
            return;
        }
        // note code nhu nay nhe ban: theo ma tran quyen, QUAN_LY chi duoc quan ly NHAN_VIEN, QUAN_TRI quan ly tat ca
        if ("QUAN_LY".equals(toi.getVaiTro()) && !"NHAN_VIEN".equals(nv.getVaiTro())) {
            chuyenHuongLoi(req, resp, duongDan, "Quản lý chỉ được thao tác với tài khoản nhân viên");
            return;
        }

        if ("khoa".equals(hanhDong)) {
            String lyDo = docChuoi(req, "lyDo").isEmpty() ? "Quản trị viên khóa tài khoản" : docChuoi(req, "lyDo");
            nv.setTrangThai("KHOA");
            nv.setLyDoKhoa(lyDo.length() > 255 ? lyDo.substring(0, 255) : lyDo);
            nv.setNgayKhoa(LocalDateTime.now());
            repo.capNhat(nv);
            chuyenHuong(req, resp, duongDan, "Đã khóa tài khoản " + nv.getHoTen());
        } else if ("mo-khoa".equals(hanhDong)) {
            nv.setTrangThai(nv.getNgayXacThucThuDienTu() != null ? "HOAT_DONG" : "CHUA_XAC_THUC");
            nv.setSoLanDangNhapSai(0);
            nv.setLyDoKhoa(null);
            nv.setNgayKhoa(null);
            repo.capNhat(nv);
            chuyenHuong(req, resp, duongDan, "Đã mở khóa tài khoản " + nv.getHoTen());
        } else {
            chuyenHuongLoi(req, resp, duongDan, "Hành động không hợp lệ");
        }
    }
}
