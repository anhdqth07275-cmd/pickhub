package com.pickhub.util;

import java.math.BigDecimal;

/**
 * Cong cu tao chuoi JSON thu cong (khong can thu vien ngoai). Chi dung cho cac API nho cua man hinh ban hang.
 */
public class JsonUtil {

    private JsonUtil() {
    }

    /** Dua chuoi vao dang "...": thoat dau ngoac kep, xuong dong, ky tu dieu khien. */
    public static String chuoi(String giaTri) {
        if (giaTri == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < giaTri.length(); i++) {
            char c = giaTri.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    break;
                case '\t':
                    sb.append(' ');
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(' ');
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.append('"').toString();
    }

    public static String so(BigDecimal giaTri) {
        return giaTri == null ? "null" : giaTri.toPlainString();
    }

    public static String so(long giaTri) {
        return String.valueOf(giaTri);
    }

    /** {"ok":false,"thongBao":"..."} */
    public static String loi(String thongBao) {
        return "{\"ok\":false,\"thongBao\":" + chuoi(thongBao) + "}";
    }
}
