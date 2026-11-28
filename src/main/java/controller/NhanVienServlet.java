package controller;

import dto.BoLoc;
import dto.TaiKhoanDangNhap;
import entity.NhanVien;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.NhanVienRepository;
import util.MatKhauUtil;
import util.PhanQuyenUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/** Quan ly nhan vien trong Admin. */
@WebServlet("/wp-admin/nhan-vien")
public class NhanVienServlet extends BaseServlet {
    private final NhanVienRepository repo = new NhanVienRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TaiKhoanDangNhap toi = taiKhoan(req);
        String action = docChuoi(req, "hanhDong");

        if ("sua".equals(action)) {
            Long id = docLong(req, "id");
            NhanVien nv = repo.timTheoId(id);
            if (nv == null) {
                chuyenHuongLoi(req, resp, "/wp-admin/nhan-vien", "Không tìm thấy nhân viên");
                return;
            }
            if (nv.getMaNhanVien().equals(toi.getId())
                    || !PhanQuyenUtil.duocQuanLyNhanVien(toi.getVaiTro(), nv.getVaiTro())) {
                chuyenHuongLoi(req, resp, "/wp-admin/nhan-vien", "Bạn không có quyền chỉnh sửa tài khoản này");
                return;
            }
            req.setAttribute("nhanVien", nv);
            req.setAttribute("laQuanTri", "QUAN_TRI".equals(toi.getVaiTro()));
            hienThi(req, resp, "nhan-vien-form", "nhan-vien", "Chỉnh sửa nhân viên");
            return;
        }

        BoLoc loc = new BoLoc().cotTim("hoTen,thuDienTu,soDienThoai")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai").trangThai(docChuoi(req, "trangThai"))
                .sapXep("x.maNhanVien asc").trang(docSo(req, "trang", 1)).kichThuoc(10);

        // Quan ly chi duoc xem tai khoan NHAN_VIEN; quan tri duoc xem tat ca.
        if (!"QUAN_TRI".equals(toi.getVaiTro())) {
            loc.dieuKienThem("x.vaiTro = 'NHAN_VIEN'");
        }
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/nhan-vien");
        hienThi(req, resp, "nhan-vien", "nhan-vien", "Quản lý nhân viên");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TaiKhoanDangNhap toi = taiKhoan(req);
        String action = docChuoi(req, "hanhDong");
        String listUrl = "/wp-admin/nhan-vien";

        try {
            Long id = docLong(req, "id");
            NhanVien nv = repo.timTheoId(id);
            if (nv == null) throw new NghiepVuException("Không tìm thấy nhân viên");

            if (nv.getMaNhanVien().equals(toi.getId())) {
                throw new NghiepVuException("Bạn không thể tự khóa hoặc thay đổi quyền, mật khẩu của chính mình tại đây");
            }
            if (!PhanQuyenUtil.duocQuanLyNhanVien(toi.getVaiTro(), nv.getVaiTro())) {
                throw new NghiepVuException("Bạn không có quyền thao tác với tài khoản này");
            }

            if ("khoa".equals(action)) {
                String lyDo = docChuoi(req, "lyDo");
                if (lyDo.isEmpty()) throw new NghiepVuException("Vui lòng nhập lý do khóa");
                nv.setTrangThai("KHOA");
                nv.setLyDoKhoa(lyDo.length() > 255 ? lyDo.substring(0, 255) : lyDo);
                nv.setNgayKhoa(LocalDateTime.now());
            } else if ("mo-khoa".equals(action)) {
                nv.setTrangThai("HOAT_DONG");
                nv.setSoLanDangNhapSai(0);
                nv.setLyDoKhoa(null);
                nv.setNgayKhoa(null);
            } else if ("cap-nhat".equals(action)) {
                capNhatThongTin(req, nv, toi);
            } else if ("dat-lai-mat-khau".equals(action)) {
                if (!"QUAN_TRI".equals(toi.getVaiTro())) {
                    throw new NghiepVuException("Chỉ quản trị viên được đặt lại mật khẩu nhân viên");
                }
                String mk = req.getParameter("matKhauMoi");
                String xacNhan = req.getParameter("xacNhanMatKhau");
                kiemTraMatKhau(mk);
                if (xacNhan == null || !mk.equals(xacNhan)) {
                    throw new NghiepVuException("Mật khẩu xác nhận không khớp");
                }
                nv.setMatKhauMaHoa(MatKhauUtil.bam(mk));
                nv.setSoLanDangNhapSai(0);
            } else {
                throw new NghiepVuException("Hành động không hợp lệ");
            }

            repo.capNhat(nv);
            if ("cap-nhat".equals(action) || "dat-lai-mat-khau".equals(action)) {
                chuyenHuong(req, resp, listUrl + "?hanhDong=sua&id=" + id,
                        "Đã cập nhật thông tin nhân viên " + nv.getHoTen());
            } else {
                chuyenHuong(req, resp, listUrl, "Đã cập nhật tài khoản " + nv.getHoTen());
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, listUrl, e.getMessage());
        } catch (Exception e) {
            chuyenHuongLoi(req, resp, listUrl, "Dữ liệu không hợp lệ hoặc email/số điện thoại đã tồn tại");
        }
    }

    private void capNhatThongTin(HttpServletRequest req, NhanVien nv, TaiKhoanDangNhap toi) {
        String hoTen = docChuoi(req, "hoTen");
        if (hoTen.length() < 2 || hoTen.length() > 100) {
            throw new NghiepVuException("Họ tên phải từ 2 đến 100 ký tự");
        }

        String email = docChuoi(req, "email").toLowerCase(Locale.ROOT);
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new NghiepVuException("Email không hợp lệ");
        }
        String sdt = docChuoi(req, "soDienThoai");
        if (!sdt.matches("^[0-9]{9,15}$")) throw new NghiepVuException("Số điện thoại không hợp lệ");

        if (repo.daTonTai("thuDienTu", email, nv.getMaNhanVien())) {
            throw new NghiepVuException("Email đã được sử dụng");
        }
        if (repo.daTonTai("soDienThoai", sdt, nv.getMaNhanVien())) {
            throw new NghiepVuException("Số điện thoại đã được sử dụng");
        }

        nv.setHoTen(hoTen);
        nv.setThuDienTu(email);
        nv.setSoDienThoai(sdt);

        String ngaySinh = docChuoi(req, "ngaySinh");
        if (ngaySinh.isEmpty()) {
            nv.setNgaySinh(null);
        } else {
            LocalDate ns = LocalDate.parse(ngaySinh);
            if (ns.isAfter(LocalDate.now())) throw new NghiepVuException("Ngày sinh không được ở tương lai");
            nv.setNgaySinh(ns);
        }

        if ("QUAN_TRI".equals(toi.getVaiTro())) {
            String role = docChuoi(req, "vaiTro");
            kiemTraVaiTro(role);
            nv.setVaiTro(role);
        } else if (!"NHAN_VIEN".equals(nv.getVaiTro())) {
            throw new NghiepVuException("Quản lý chỉ được chỉnh sửa thông tin nhân viên");
        }
    }

    private void kiemTraVaiTro(String role) {
        if (!"NHAN_VIEN".equals(role) && !"QUAN_LY".equals(role) && !"QUAN_TRI".equals(role)) {
            throw new NghiepVuException("Vai trò không hợp lệ");
        }
    }

    private void kiemTraMatKhau(String mk) {
        if (mk == null || mk.length() < 8 || !mk.matches(".*[A-Za-z].*") || !mk.matches(".*[0-9].*")) {
            throw new NghiepVuException("Mật khẩu phải có ít nhất 8 ký tự, gồm chữ và số");
        }
    }
}
