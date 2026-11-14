package util;

import exception.NghiepVuException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Doi chuoi nguoi dung nhap tren form thanh so / ngay gio.
 * O trong -> tra ve null. Nhap sai dinh dang -> NghiepVuException kem ten truong de hien thi cho nguoi dung.
 */
public class NhapLieuUtil {

    // input type="datetime-local" gui len dang "2026-11-09T00:00" (co the kem giay)
    public static LocalDateTime docThoiGian(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(chuoi.trim());
        } catch (DateTimeParseException e) {
            throw new NghiepVuException(tenTruong + " không đúng định dạng ngày giờ");
        }
    }

    public static BigDecimal docSo(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.trim().isEmpty()) return null;
        try {
            // cho phep go dau phay kieu Viet Nam (15,5)
            return new BigDecimal(chuoi.trim().replace(" ", "").replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new NghiepVuException(tenTruong + " phải là số");
        }
    }

    public static Integer docSoNguyen(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.trim().isEmpty()) return null;
        try {
            return Integer.valueOf(chuoi.trim());
        } catch (NumberFormatException e) {
            throw new NghiepVuException(tenTruong + " phải là số nguyên");
        }
    }
}
