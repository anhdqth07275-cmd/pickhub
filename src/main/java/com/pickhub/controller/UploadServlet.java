package com.pickhub.controller;

import com.pickhub.util.UploadUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/** Tra anh san pham da tai len: GET /uploads/... (file nam trong thu muc UploadUtil.thuMucGoc(), ngoai webapp). Khong co file -> 404. */
@WebServlet("/uploads/*")
public class UploadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws IOException {
        String phan = yeuCau.getPathInfo();
        File file = phan == null ? null : UploadUtil.fileTuDuongDan("/uploads" + phan);
        if (file == null || !file.isFile()) {
            phanHoi.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String ten = file.getName().toLowerCase();
        String loai = null;
        if (ten.endsWith(".png")) {
            loai = "image/png";
        } else if (ten.endsWith(".webp")) {
            loai = "image/webp";
        } else if (ten.endsWith(".jpg") || ten.endsWith(".jpeg")) {
            loai = "image/jpeg";
        }
        if (loai == null) {
            phanHoi.sendError(HttpServletResponse.SC_NOT_FOUND);        // chi phuc vu 3 loai anh cho phep
            return;
        }
        phanHoi.setContentType(loai);
        phanHoi.setHeader("Cache-Control", "public, max-age=86400");
        phanHoi.setContentLengthLong(file.length());
        try (OutputStream ra = phanHoi.getOutputStream()) {
            Files.copy(file.toPath(), ra);
        }
    }
}
