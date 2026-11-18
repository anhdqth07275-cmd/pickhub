package com.pickhub.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/** Kiem tra mat khau bang BCrypt (database chi luu ma bam, khong luu mat khau that). */
public class MatKhauUtil {

    private MatKhauUtil() {
    }

    public static boolean kiemTra(String matKhauNhap, String maBam) {
        if (matKhauNhap == null || maBam == null) {
            return false;
        }
        try {
            return BCrypt.verifyer().verify(matKhauNhap.toCharArray(), maBam).verified;
        } catch (RuntimeException loi) {
            return false;      // ma bam hong dinh dang thi coi nhu sai mat khau
        }
    }

    public static String bam(String matKhau) {
        return BCrypt.withDefaults().hashToString(10, matKhau.toCharArray());
    }
}
