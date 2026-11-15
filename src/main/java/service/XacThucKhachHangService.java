package service;

import dto.TaiKhoanKhachHang;
import entity.KhachHang;
import exception.NghiepVuException;
import org.hibernate.LockMode;
import repository.KhachHangRepository;
import util.GiaoDich;
import util.MatKhauUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/** Nghiep vu dang ky, dang nhap va cap nhat tai khoan khach hang (M1), khong su dung OTP. */
public class XacThucKhachHangService {
    private static final int SO_LAN_SAI_TOI_DA = 5;
    private final KhachHangRepository repo = new KhachHangRepository();

    public void dangKy(String hoTen, String email, String sdt, String matKhau, String nhapLai) {
        hoTen = hoTen == null ? "" : hoTen.trim();
        email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        sdt = sdt == null ? "" : sdt.trim();

        kiemTraHoTen(hoTen);
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new NghiepVuException("Email không hợp lệ");
        }
        kiemTraSoDienThoai(sdt);
        kiemTraMatKhau(matKhau, nhapLai);

        if (repo.daTonTai("thuDienTu", email, null)) throw new NghiepVuException("Email đã được sử dụng");
        if (repo.daTonTai("soDienThoai", sdt, null)) throw new NghiepVuException("Số điện thoại đã được sử dụng");

        // note code nhu nay nhe ban: bo OTP theo yeu cau; khach hang moi duoc kich hoat ngay sau khi dang ky.
        KhachHang kh = new KhachHang();
        kh.setHoTen(hoTen);
        kh.setThuDienTu(email);
        kh.setSoDienThoai(sdt);
        kh.setMatKhauMaHoa(MatKhauUtil.bam(matKhau));
        kh.setTrangThai("HOAT_DONG");
        kh.setNgayXacThucThuDienTu(LocalDateTime.now());
        kh.setSoLanDangNhapSai(0);
        repo.luu(kh);
    }

    public TaiKhoanKhachHang dangNhap(String email, String matKhau) {
        final String e = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (e.isEmpty() || matKhau == null || matKhau.isEmpty()) {
            throw new NghiepVuException("Vui lòng nhập email và mật khẩu");
        }

        final String[] loi = {null};
        TaiKhoanKhachHang tk = GiaoDich.chay(session -> {
            // note code nhu nay nhe ban: khoa ban ghi de bo dem dang nhap sai khong bi ghi de boi request khac.
            KhachHang kh = session.createQuery(
                            "from KhachHang k where lower(k.thuDienTu) = :e", KhachHang.class)
                    .setParameter("e", e)
                    .setLockMode("k", LockMode.PESSIMISTIC_WRITE)
                    .uniqueResult();

            if (kh == null) {
                loi[0] = "Sai email hoặc mật khẩu";
                return null;
            }
            if (!"HOAT_DONG".equals(kh.getTrangThai())) {
                loi[0] = "KHOA".equals(kh.getTrangThai())
                        ? "Tài khoản đã bị khóa: " + (kh.getLyDoKhoa() == null ? "Vui lòng liên hệ hỗ trợ" : kh.getLyDoKhoa())
                        : "Tài khoản chưa được kích hoạt. Vui lòng liên hệ hỗ trợ.";
                return null;
            }

            if (!MatKhauUtil.kiemTra(matKhau, kh.getMatKhauMaHoa())) {
                int dem = kh.getSoLanDangNhapSai() == null ? 1 : kh.getSoLanDangNhapSai() + 1;
                kh.setSoLanDangNhapSai(dem);
                if (dem >= SO_LAN_SAI_TOI_DA) {
                    kh.setTrangThai("KHOA");
                    kh.setLyDoKhoa("Nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần");
                    kh.setNgayKhoa(LocalDateTime.now());
                    loi[0] = "Tài khoản đã bị khóa do nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần";
                } else {
                    loi[0] = "Sai email hoặc mật khẩu (còn " + (SO_LAN_SAI_TOI_DA - dem) + " lần thử)";
                }
                return null; // note code: return null de commit bo dem sai, khong throw rollback.
            }

            kh.setSoLanDangNhapSai(0);
            kh.setNgayDangNhapCuoi(LocalDateTime.now());
            return new TaiKhoanKhachHang(kh.getMaKhachHang(), kh.getHoTen(),
                    kh.getThuDienTu(), kh.getDuongDanAnhDaiDien());
        });

        if (tk == null) throw new NghiepVuException(loi[0] == null ? "Không thể đăng nhập" : loi[0]);
        return tk;
    }

    public void capNhatHoSo(Long id, String hoTen, String sdt, LocalDate ngaySinh) {
        hoTen = hoTen == null ? "" : hoTen.trim();
        sdt = sdt == null ? "" : sdt.trim();
        kiemTraHoTen(hoTen);
        kiemTraSoDienThoai(sdt);
        kiemTraNgaySinh(ngaySinh);

        KhachHang kh = repo.timTheoId(id);
        if (kh == null) throw new NghiepVuException("Không tìm thấy tài khoản");
        if (repo.daTonTai("soDienThoai", sdt, id)) throw new NghiepVuException("Số điện thoại đã được sử dụng");
        kh.setHoTen(hoTen);
        kh.setSoDienThoai(sdt);
        kh.setNgaySinh(ngaySinh);
        repo.capNhat(kh);
    }

    public void doiMatKhau(Long id, String matKhauCu, String matKhauMoi, String nhapLai) {
        KhachHang kh = repo.timTheoId(id);
        if (kh == null) throw new NghiepVuException("Không tìm thấy tài khoản");
        if (!MatKhauUtil.kiemTra(matKhauCu, kh.getMatKhauMaHoa())) {
            throw new NghiepVuException("Mật khẩu hiện tại không đúng");
        }
        kiemTraMatKhau(matKhauMoi, nhapLai);
        kh.setMatKhauMaHoa(MatKhauUtil.bam(matKhauMoi));
        kh.setSoLanDangNhapSai(0);
        repo.capNhat(kh);
    }

    private void kiemTraHoTen(String hoTen) {
        if (hoTen == null || hoTen.trim().length() < 2 || hoTen.trim().length() > 100) {
            throw new NghiepVuException("Họ tên phải từ 2 đến 100 ký tự");
        }
    }

    private void kiemTraSoDienThoai(String sdt) {
        if (sdt == null || !sdt.matches("^[0-9]{9,15}$")) {
            throw new NghiepVuException("Số điện thoại phải có từ 9 đến 15 chữ số");
        }
    }

    private void kiemTraNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh != null && ngaySinh.isAfter(LocalDate.now())) {
            throw new NghiepVuException("Ngày sinh không được ở tương lai");
        }
    }

    private void kiemTraMatKhau(String matKhau, String nhapLai) {
        if (matKhau == null || matKhau.length() < 8) {
            throw new NghiepVuException("Mật khẩu phải có ít nhất 8 ký tự");
        }
        boolean chu = false;
        boolean so = false;
        for (char c : matKhau.toCharArray()) {
            if (Character.isLetter(c)) chu = true;
            if (Character.isDigit(c)) so = true;
        }
        if (!chu || !so) throw new NghiepVuException("Mật khẩu phải có cả chữ và số");
        if (!matKhau.equals(nhapLai)) throw new NghiepVuException("Mật khẩu nhập lại không khớp");
    }
}
