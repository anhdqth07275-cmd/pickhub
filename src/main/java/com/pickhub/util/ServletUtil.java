package com.pickhub.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/** Ham tien ich dung chung cho cac Servlet: doc tham so an toan va tra JSON. */
public class ServletUtil {

    private ServletUtil() {
    }

    /** Doc tham so dang chuoi, bo khoang trang 2 dau; khong co thi tra chuoi rong (khong bao gio null). */
    public static String docChuoi(HttpServletRequest yeuCau, String ten) {
        String giaTri = yeuCau.getParameter(ten);
        return giaTri == null ? "" : giaTri.trim();
    }

    /** Doc tham so dang so nguyen lon; rong hoac sai dinh dang thi tra null. */
    public static Long docLong(HttpServletRequest yeuCau, String ten) {
        String giaTri = docChuoi(yeuCau, ten);
        if (giaTri.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(giaTri);
        } catch (NumberFormatException loi) {
            return null;
        }
    }

    public static int docSo(HttpServletRequest yeuCau, String ten, int macDinh) {
        String giaTri = docChuoi(yeuCau, ten);
        if (giaTri.isEmpty()) {
            return macDinh;
        }
        try {
            return Integer.parseInt(giaTri);
        } catch (NumberFormatException loi) {
            return macDinh;
        }
    }

    public static void guiJson(HttpServletResponse phanHoi, int maTrangThai, String json) throws IOException {
        phanHoi.setStatus(maTrangThai);
        phanHoi.setContentType("application/json;charset=UTF-8");
        phanHoi.setHeader("Cache-Control", "no-store");
        PrintWriter viet = phanHoi.getWriter();
        viet.write(json);
        viet.flush();
    }
}
