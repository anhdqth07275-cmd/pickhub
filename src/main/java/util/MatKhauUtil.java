package util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/** Bam va kiem tra mat khau bang BCrypt. */
public final class MatKhauUtil {

    private MatKhauUtil() {
        // note code: lop tien ich chi chua ham static, khong can tao doi tuong.
    }

    public static String bam(String matKhau) {
        if (matKhau == null) throw new IllegalArgumentException("Mật khẩu không được để trống");
        return BCrypt.withDefaults().hashToString(10, matKhau.toCharArray());
    }

    // note code nhu nay nhe ban: so khop mat khau nhap vao voi ma bam BCrypt luu trong database.
    public static boolean kiemTra(String matKhau, String maBam) {
        if (matKhau == null || maBam == null || maBam.trim().isEmpty()) return false;
        try {
            return BCrypt.verifyer().verify(matKhau.toCharArray(), maBam).verified;
        } catch (IllegalArgumentException e) {
            // note code: du lieu hash cu bi loi thi coi nhu khong khop, tranh lam loi ca trang dang nhap.
            return false;
        }
    }
}
