package util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Bam / kiem tra mat khau bang BCrypt. Thu vien at.favre doc duoc ma bam $2b$ trong du lieu mau.
 */
public class MatKhauUtil {

    public static String bam(String matKhau) {
        return BCrypt.withDefaults().hashToString(10, matKhau.toCharArray());
    }

    // note code nhu nay nhe ban: so khop mat khau nhap vao voi ma bam luu trong DB (cot matKhauMaHoa)
    public static boolean kiemTra(String matKhau, String maBam) {
        if (matKhau == null || maBam == null) return false;
        return BCrypt.verifyer().verify(matKhau.toCharArray(), maBam).verified;
    }
}
