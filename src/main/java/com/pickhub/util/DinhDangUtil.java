package com.pickhub.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Dinh dang tien (1.250.000 d) va ngay gio (dd/MM/yyyy HH:mm) de hien tren man hinh / hoa don in. */
public class DinhDangUtil {

    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter NGAY_GIO_GIAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter GIO_PHUT = DateTimeFormatter.ofPattern("HH:mm");

    private DinhDangUtil() {
    }

    /** 1250000 -> "1.250.000" (khong kem chu d, JSP tu them). */
    public static String tien(BigDecimal soTien) {
        if (soTien == null) {
            return "0";
        }
        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols(Locale.US);
        kyHieu.setGroupingSeparator('.');
        return new DecimalFormat("#,##0", kyHieu).format(soTien);
    }

    public static String ngayGio(LocalDateTime thoiGian) {
        return thoiGian == null ? "" : thoiGian.format(NGAY_GIO);
    }

    public static String ngayGioGiay(LocalDateTime thoiGian) {
        return thoiGian == null ? "" : thoiGian.format(NGAY_GIO_GIAY);
    }

    public static String gioPhut(LocalDateTime thoiGian) {
        return thoiGian == null ? "" : thoiGian.format(GIO_PHUT);
    }

    /** Doc so tu chuoi nguoi dung nhap ("1.500.000" hoac "1500000"), rong thi tra null, sai thi nem loi. */
    public static BigDecimal docTien(String chuoi, String tenTruong) {
        if (chuoi == null || chuoi.trim().isEmpty()) {
            return null;
        }
        try {
            String sach = chuoi.trim().replace(".", "").replace(",", "").replace(" ", "");
            return new BigDecimal(sach);
        } catch (NumberFormatException loi) {
            throw new NghiepVuException(tenTruong + " phải là số");
        }
    }
}
