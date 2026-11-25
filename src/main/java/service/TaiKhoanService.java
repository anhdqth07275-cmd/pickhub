package service;

import entity.NhanVien;
import exception.NghiepVuException;
import repository.NhanVienRepository;
import util.MatKhauUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Nghiep vu TAI KHOAN CA NHAN cua nhan vien dang nhap o /wp-admin: sua ho so + doi mat khau.
 * Servlet (TaiKhoanServlet) chi doc tham so roi goi 2 ham o day; moi luat kiem tra nam o day.
 */
public class TaiKhoanService {

    private final NhanVienRepository repo = new NhanVienRepository();

    public NhanVien layTheoId(Long maNhanVien) {
        return repo.timTheoId(maNhanVien);
    }

    // =====================================================================
    // 1. CHINH SUA THONG TIN CA NHAN
    // =====================================================================
    /** Tra ve nhan vien sau khi luu de servlet lam moi thong tin (ho ten...) trong session. */
    public NhanVien capNhatHoSo(Long maNhanVien, String hoTen, String soDienThoai, String ngaySinh) {
        NhanVien nv = repo.timTheoId(maNhanVien);
        if (nv == null) throw new NghiepVuException("Không tìm thấy tài khoản");

        hoTen = hoTen == null ? "" : hoTen.trim();
        soDienThoai = soDienThoai == null ? "" : soDienThoai.trim();
        ngaySinh = ngaySinh == null ? "" : ngaySinh.trim();

        if (hoTen.length() < 2 || hoTen.length() > 100) throw new NghiepVuException("Họ tên phải từ 2 đến 100 ký tự");
        // note code nhu nay nhe ban: so dien thoai chi gom chu so, 9-15 so (cot SoDienThoai trong DB la VARCHAR(15))
        if (!soDienThoai.matches("^[0-9]{9,15}$")) throw new NghiepVuException("Số điện thoại không hợp lệ (9 - 15 chữ số)");
        // note code nhu nay nhe ban: truyen maNhanVien de KHONG tinh trung voi chinh minh (sua ho so ma giu nguyen SDT van hop le)
        if (repo.daTonTai("soDienThoai", soDienThoai, nv.getMaNhanVien())) throw new NghiepVuException("Số điện thoại đã được sử dụng");

        LocalDate ngay = null;
        if (!ngaySinh.isEmpty()) {
            try {
                ngay = LocalDate.parse(ngaySinh);
            } catch (DateTimeParseException e) {
                throw new NghiepVuException("Ngày sinh không hợp lệ");
            }
            if (ngay.isAfter(LocalDate.now())) throw new NghiepVuException("Ngày sinh không được ở tương lai");
        }

        nv.setHoTen(hoTen);
        nv.setSoDienThoai(soDienThoai);
        nv.setNgaySinh(ngay);
        repo.capNhat(nv);
        return nv;
    }

    // =====================================================================
    // 2. DOI MAT KHAU
    // =====================================================================
    public void doiMatKhau(Long maNhanVien, String matKhauCu, String matKhauMoi, String nhapLai) {
        NhanVien nv = repo.timTheoId(maNhanVien);
        if (nv == null) throw new NghiepVuException("Không tìm thấy tài khoản");

        // note code nhu nay nhe ban: KHONG trim mat khau - khoang trang co the la mot phan cua mat khau
        if (!MatKhauUtil.kiemTra(matKhauCu, nv.getMatKhauMaHoa())) throw new NghiepVuException("Mật khẩu hiện tại không đúng");
        if (matKhauMoi == null || matKhauMoi.length() < 8 || !matKhauMoi.matches(".*[A-Za-z].*") || !matKhauMoi.matches(".*[0-9].*")) {
            throw new NghiepVuException("Mật khẩu mới phải có ít nhất 8 ký tự, gồm cả chữ và số");
        }
        if (!matKhauMoi.equals(nhapLai)) throw new NghiepVuException("Mật khẩu nhập lại không khớp");
        if (matKhauMoi.equals(matKhauCu)) throw new NghiepVuException("Mật khẩu mới phải khác mật khẩu hiện tại");

        nv.setMatKhauMaHoa(MatKhauUtil.bam(matKhauMoi));     // luu ma BCrypt, khong bao gio luu mat khau that
        nv.setSoLanDangNhapSai(0);
        repo.capNhat(nv);
    }
}
