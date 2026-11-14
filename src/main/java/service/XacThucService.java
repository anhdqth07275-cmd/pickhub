package service;

import dto.KhachHangDangNhap;
import dto.TaiKhoanDangNhap;
import entity.KhachHang;
import entity.NhanVien;
import exception.NghiepVuException;
import util.GiaoDich;
import util.MatKhauUtil;

import java.time.LocalDateTime;

/**
 * Dang nhap nhan vien (ban toi gian cho trang /wp-admin, module M2 se hoan thien them: quen mat khau, xac thuc email...).
 */
public class XacThucService {

    private static final int SO_LAN_SAI_TOI_DA = 5;

    public TaiKhoanDangNhap dangNhap(String email, String matKhau) {
        if (email.isEmpty() || matKhau.isEmpty()) throw new NghiepVuException("Vui lòng nhập email và mật khẩu");

        final String[] loi = new String[1];   // chua thong bao loi khi sai mat khau (can commit truoc khi bao loi)

        TaiKhoanDangNhap ketQua = GiaoDich.chay(session -> {
            NhanVien nv = session.createQuery("from NhanVien n where lower(n.thuDienTu) = :e", NhanVien.class)
                    .setParameter("e", email.toLowerCase()).uniqueResult();
            if (nv == null) throw new NghiepVuException("Sai email hoặc mật khẩu");

            // note code nhu nay nhe ban: bao ro neu tai khoan bi khoa / chua xac thuc
            if ("KHOA".equals(nv.getTrangThai())) throw new NghiepVuException("Tài khoản đã bị khóa: " + nv.getLyDoKhoa());
            if ("CHUA_XAC_THUC".equals(nv.getTrangThai())) throw new NghiepVuException("Tài khoản chưa xác thực email");

            if (!MatKhauUtil.kiemTra(matKhau, nv.getMatKhauMaHoa())) {
                // note code nhu nay nhe ban: sai mat khau +1, du 5 lan thi KHOA (ghi lyDoKhoa + ngayKhoa)
                int dem = nv.getSoLanDangNhapSai() + 1;
                nv.setSoLanDangNhapSai(dem);
                if (dem >= SO_LAN_SAI_TOI_DA) {
                    nv.setTrangThai("KHOA");
                    nv.setLyDoKhoa("Nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần");
                    nv.setNgayKhoa(LocalDateTime.now());
                    loi[0] = "Tài khoản đã bị khóa do nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần";
                } else {
                    loi[0] = "Sai email hoặc mật khẩu (còn " + (SO_LAN_SAI_TOI_DA - dem) + " lần thử)";
                }
                return null;    // return thay vi throw de transaction van COMMIT bo dem
            }

            // dung mat khau: reset bo dem + ghi lan dang nhap cuoi
            nv.setSoLanDangNhapSai(0);
            nv.setNgayDangNhapCuoi(LocalDateTime.now());
            return new TaiKhoanDangNhap(nv.getMaNhanVien(), nv.getHoTen(), nv.getThuDienTu(), nv.getVaiTro(), nv.getDuongDanAnhDaiDien());
        });

        if (ketQua == null) throw new NghiepVuException(loi[0]);
        return ketQua;
    }

    /**
     * Dang nhap KHACH HANG (trang /dang-nhap). Cung luat voi nhan vien: sai 5 lan thi KHOA.
     */
    public KhachHangDangNhap dangNhapKhachHang(String email, String matKhau) {
        if (email.isEmpty() || matKhau.isEmpty()) throw new NghiepVuException("Vui lòng nhập email và mật khẩu");
        final String[] loi = new String[1];

        KhachHangDangNhap ketQua = GiaoDich.chay(session -> {
            KhachHang kh = session.createQuery("from KhachHang k where lower(k.thuDienTu) = :e", KhachHang.class)
                    .setParameter("e", email.toLowerCase()).uniqueResult();
            if (kh == null) throw new NghiepVuException("Sai email hoặc mật khẩu");
            if ("KHOA".equals(kh.getTrangThai())) throw new NghiepVuException("Tài khoản đã bị khóa: " + kh.getLyDoKhoa());
            if ("CHUA_XAC_THUC".equals(kh.getTrangThai())) throw new NghiepVuException("Tài khoản chưa xác thực email");

            if (!MatKhauUtil.kiemTra(matKhau, kh.getMatKhauMaHoa())) {
                int dem = kh.getSoLanDangNhapSai() + 1;
                kh.setSoLanDangNhapSai(dem);
                if (dem >= SO_LAN_SAI_TOI_DA) {
                    kh.setTrangThai("KHOA");
                    kh.setLyDoKhoa("Nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần");
                    kh.setNgayKhoa(LocalDateTime.now());
                    loi[0] = "Tài khoản đã bị khóa do nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần";
                } else {
                    loi[0] = "Sai email hoặc mật khẩu (còn " + (SO_LAN_SAI_TOI_DA - dem) + " lần thử)";
                }
                return null;
            }
            kh.setSoLanDangNhapSai(0);
            kh.setNgayDangNhapCuoi(LocalDateTime.now());
            return new KhachHangDangNhap(kh.getMaKhachHang(), kh.getHoTen(), kh.getThuDienTu(), kh.getSoDienThoai());
        });

        if (ketQua == null) throw new NghiepVuException(loi[0]);
        return ketQua;
    }
}
