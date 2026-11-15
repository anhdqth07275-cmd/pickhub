package util;

import exception.NghiepVuException;

import java.math.BigDecimal;

/**
 * Doi chuoi nguoi dung nhap thanh so. Nhap sai thi bao loi tieng Viet dung ten truong (khong de ung dung vang loi).
 * Chuoi rong -> tra ve null (truong khong bat buoc).
 */
public class DocSoUtil {

    public static Integer soNguyen(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.trim().isEmpty()) return null;
        try {
            return Integer.valueOf(chuoi.trim());
        } catch (NumberFormatException e) {
            throw new NghiepVuException(tenTruong + " phải là số nguyên");
        }
    }

    public static BigDecimal soThapPhan(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.trim().isEmpty()) return null;
        try {
            // note code nhu nay nhe ban: cho phep nguoi dung go "1.500.000" hoac "1,5" -> bo dau cham ngan cach, doi phay thanh cham
            String s = chuoi.trim().replace(" ", "");
            if (s.matches("\\d{1,3}(\\.\\d{3})+")) s = s.replace(".", "");
            return new BigDecimal(s.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new NghiepVuException(tenTruong + " phải là số");
        }
    }
}
