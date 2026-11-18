package com.pickhub.util;

import java.io.File;
import java.io.IOException;

/**
 * Thu muc chua anh san pham da tai len (mac dinh ~/pickhub-uploads, doi bang -Dpickhub.uploads=D:/anh).
 * Anh duoc phuc vu qua duong dan /uploads/... (xem UploadServlet).
 */
public class UploadUtil {

    private UploadUtil() {
    }

    public static File thuMucGoc() {
        String cauHinh = System.getProperty("pickhub.uploads");
        File thuMuc = (cauHinh != null && !cauHinh.isEmpty()) ? new File(cauHinh) : new File(System.getProperty("user.home"), "pickhub-uploads");
        thuMuc.mkdirs();
        return thuMuc;
    }

    /** Doi duong dan web "/uploads/a/b.jpg" thanh File that; tra null neu duong dan la hoac co ky tu nguy hiem ("..") */
    public static File fileTuDuongDan(String duongDan) {
        if (duongDan == null || !duongDan.startsWith("/uploads/")) {
            return null;
        }
        String tuongDoi = duongDan.substring("/uploads/".length());
        if (tuongDoi.isEmpty() || tuongDoi.contains("..") || tuongDoi.contains("\\")) {
            return null;
        }
        File goc = thuMucGoc();
        File file = new File(goc, tuongDoi);
        try {
            if (!file.getCanonicalPath().startsWith(goc.getCanonicalPath())) {
                return null;
            }
        } catch (IOException loi) {
            return null;
        }
        return file;
    }
}
