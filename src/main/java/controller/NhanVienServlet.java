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

/** Quan ly tai khoan nhan vien. QUAN_LY chi duoc quan ly NHAN_VIEN; QUAN_TRI quan ly ca 3 cap. */
@WebServlet("/wp-admin/nhan-vien")
public class NhanVienServlet extends BaseServlet {
    private final NhanVienRepository repo = new NhanVienRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc().cotTim("hoTen,thuDienTu,soDienThoai").tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai").trangThai(docChuoi(req, "trangThai"))
                .sapXep("x.maNhanVien asc").trang(docSo(req, "trang", 1)).kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/nhan-vien");
        req.setAttribute("coTheTaoQuanLy", "QUAN_TRI".equals(taiKhoan(req).getVaiTro()));
        hienThi(req, resp, "nhan-vien", "nhan-vien", "Quản lý nhân viên");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TaiKhoanDangNhap toi = taiKhoan(req);
        String action = docChuoi(req, "hanhDong");
        String url = "/wp-admin/nhan-vien";
        try {
            if ("them".equals(action)) {
                themNhanVien(req, toi);
                chuyenHuong(req, resp, url, "Đã tạo tài khoản nhân viên thành công");
                return;
            }

            Long id = docLong(req, "id");
            NhanVien nv = repo.timTheoId(id);
            if (nv == null) throw new NghiepVuException("Không tìm thấy nhân viên");

            // note code nhu nay nhe ban: khong cho tu khoa hoac tu ha/nang quyen cua chinh minh.
            if (nv.getMaNhanVien().equals(toi.getId())) {
                throw new NghiepVuException("Bạn không thể tự khóa hoặc thay đổi vai trò của chính mình");
            }
            if (!PhanQuyenUtil.duocQuanLyNhanVien(toi.getVaiTro(), nv.getVaiTro())) {
                throw new NghiepVuException("Quản lý chỉ được thao tác với tài khoản nhân viên");
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
            } else if ("doi-vai-tro".equals(action)) {
                if (!"QUAN_TRI".equals(toi.getVaiTro())) {
                    throw new NghiepVuException("Chỉ quản trị viên được thay đổi vai trò");
                }
                String role = docChuoi(req, "vaiTro");
                kiemTraVaiTro(role);
                nv.setVaiTro(role);
            } else if ("dat-lai-mat-khau".equals(action)) {
                if (!"QUAN_TRI".equals(toi.getVaiTro())) {
                    throw new NghiepVuException("Chỉ quản trị viên được đặt lại mật khẩu nhân viên");
                }
                String mk = req.getParameter("matKhauMoi");
                kiemTraMatKhau(mk);
                nv.setMatKhauMaHoa(MatKhauUtil.bam(mk));
                nv.setSoLanDangNhapSai(0);
            } else {
                throw new NghiepVuException("Hành động không hợp lệ");
            }

            repo.capNhat(nv);
            chuyenHuong(req, resp, url, "Đã cập nhật tài khoản " + nv.getHoTen());
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, url, e.getMessage());
        } catch (Exception e) {
            chuyenHuongLoi(req, resp, url, "Dữ liệu không hợp lệ hoặc thông tin đã tồn tại");
        }
    }

    private void themNhanVien(HttpServletRequest req, TaiKhoanDangNhap toi) {
        String vaiTro = docChuoi(req, "vaiTro");
        kiemTraVaiTro(vaiTro);
        if (!"NHAN_VIEN".equals(vaiTro) && !"QUAN_TRI".equals(toi.getVaiTro())) {
            throw new NghiepVuException("Quản lý chỉ được tạo tài khoản nhân viên");
        }

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
        if (repo.daTonTai("thuDienTu", email, null)) throw new NghiepVuException("Email đã tồn tại");
        if (repo.daTonTai("soDienThoai", sdt, null)) throw new NghiepVuException("Số điện thoại đã tồn tại");

        String mk = req.getParameter("matKhau");
        kiemTraMatKhau(mk);

        NhanVien nv = new NhanVien();
        nv.setVaiTro(vaiTro);
        nv.setHoTen(hoTen);
        nv.setThuDienTu(email);
        nv.setSoDienThoai(sdt);
        nv.setMatKhauMaHoa(MatKhauUtil.bam(mk));
        nv.setTrangThai("HOAT_DONG");
        nv.setNgayXacThucThuDienTu(LocalDateTime.now());
        nv.setSoLanDangNhapSai(0);
        nv.setMaNguoiTao(toi.getId());

        String ngaySinh = docChuoi(req, "ngaySinh");
        if (!ngaySinh.isEmpty()) {
            LocalDate ns = LocalDate.parse(ngaySinh);
            if (ns.isAfter(LocalDate.now())) throw new NghiepVuException("Ngày sinh không được ở tương lai");
            nv.setNgaySinh(ns);
        }
        repo.luu(nv);
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
