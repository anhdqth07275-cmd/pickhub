package service;

import dto.TaiKhoanDangNhap;
import entity.NhanVien;
import exception.NghiepVuException;
import org.hibernate.LockMode;
import util.GiaoDich;
import util.MatKhauUtil;

import java.time.LocalDateTime;
import java.util.Locale;

/** Xac thuc nhan vien cho trang /wp-admin. */
public class XacThucService {

    private static final int SO_LAN_SAI_TOI_DA = 5;

    public TaiKhoanDangNhap dangNhap(String email, String matKhau) {
        final String e = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (e.isEmpty() || matKhau == null || matKhau.isEmpty()) {
            throw new NghiepVuException("Vui lòng nhập email và mật khẩu");
        }

        final String[] loi = new String[1];
        TaiKhoanDangNhap ketQua = GiaoDich.chay(session -> {
            // note code nhu nay nhe ban: khoa ban ghi khi dang nhap de tranh 2 request cung tang sai mot luc.
            NhanVien nv = session.createQuery(
                            "from NhanVien n where lower(n.thuDienTu) = :e", NhanVien.class)
                    .setParameter("e", e)
                    .setLockMode("n", LockMode.PESSIMISTIC_WRITE)
                    .uniqueResult();

            if (nv == null) {
                loi[0] = "Sai email hoặc mật khẩu";
                return null;
            }

            // note code nhu nay nhe ban: chi tai khoan HOAT_DONG moi duoc dang nhap; tai khoan chua kich hoat cung bi chan.
            if (!"HOAT_DONG".equals(nv.getTrangThai())) {
                loi[0] = "KHOA".equals(nv.getTrangThai())
                        ? "Tài khoản đã bị khóa: " + (nv.getLyDoKhoa() == null ? "Vui lòng liên hệ quản trị viên" : nv.getLyDoKhoa())
                        : "Tài khoản chưa được kích hoạt. Vui lòng liên hệ quản trị viên.";
                return null;
            }

            if (!MatKhauUtil.kiemTra(matKhau, nv.getMatKhauMaHoa())) {
                // note code nhu nay nhe ban: tang bo dem sai; du 5 lan thi khoa tai khoan trong cung transaction.
                int dem = nv.getSoLanDangNhapSai() == null ? 1 : nv.getSoLanDangNhapSai() + 1;
                nv.setSoLanDangNhapSai(dem);
                if (dem >= SO_LAN_SAI_TOI_DA) {
                    nv.setTrangThai("KHOA");
                    nv.setLyDoKhoa("Nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần");
                    nv.setNgayKhoa(LocalDateTime.now());
                    loi[0] = "Tài khoản đã bị khóa do nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần";
                } else {
                    loi[0] = "Sai email hoặc mật khẩu (còn " + (SO_LAN_SAI_TOI_DA - dem) + " lần thử)";
                }
                return null; // note code: khong throw o day de transaction commit bo dem dang nhap sai.
            }

            nv.setSoLanDangNhapSai(0);
            nv.setNgayDangNhapCuoi(LocalDateTime.now());
            return new TaiKhoanDangNhap(nv.getMaNhanVien(), nv.getHoTen(), nv.getThuDienTu(),
                    nv.getVaiTro(), nv.getDuongDanAnhDaiDien());
        });

        if (ketQua == null) throw new NghiepVuException(loi[0] == null ? "Không thể đăng nhập" : loi[0]);
        return ketQua;
    }
}
