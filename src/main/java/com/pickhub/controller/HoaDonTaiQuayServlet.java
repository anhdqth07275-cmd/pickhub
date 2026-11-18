package com.pickhub.controller;

import com.pickhub.entity.NhanVien;
import com.pickhub.service.IHoaDonService;
import com.pickhub.service.impl.HoaDonServiceImpl;
import com.pickhub.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Hoa don da ban tai quay: GET /pos/hoa-don?tuKhoa=&tuNgay=&denNgay=&tatCa=1
 * Nhan vien thuong chi xem hoa don CUA MINH; QUAN_TRI / QUAN_LY xem duoc cua tat ca (tick "tatCa").
 */
@WebServlet("/pos/hoa-don")
public class HoaDonTaiQuayServlet extends HttpServlet {

    private final IHoaDonService hoaDonService = new HoaDonServiceImpl();

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws ServletException, IOException {
        NhanVien nhanVien = (NhanVien) yeuCau.getSession().getAttribute("nhanVien");
        boolean laQuanLy = "QUAN_TRI".equals(nhanVien.getVaiTro()) || "QUAN_LY".equals(nhanVien.getVaiTro());
        boolean xemTatCa = laQuanLy && "1".equals(ServletUtil.docChuoi(yeuCau, "tatCa"));
        Long locNhanVien = xemTatCa ? null : nhanVien.getMaNhanVien();       // chan nhan vien thuong xem hoa don nguoi khac

        LocalDate homNay = LocalDate.now();
        LocalDate tuNgay = docNgay(ServletUtil.docChuoi(yeuCau, "tuNgay"), homNay);
        LocalDate denNgay = docNgay(ServletUtil.docChuoi(yeuCau, "denNgay"), homNay);
        if (denNgay.isBefore(tuNgay)) {
            denNgay = tuNgay;
        }

        yeuCau.setAttribute("danhSach", hoaDonService.timHoaDonTaiQuay(ServletUtil.docChuoi(yeuCau, "tuKhoa"), tuNgay, denNgay, locNhanVien));
        yeuCau.setAttribute("tongKet", hoaDonService.tongKet(tuNgay, denNgay, locNhanVien));
        yeuCau.setAttribute("tuNgay", tuNgay.toString());
        yeuCau.setAttribute("denNgay", denNgay.toString());
        yeuCau.setAttribute("laQuanLy", laQuanLy);
        yeuCau.setAttribute("xemTatCa", xemTatCa);
        yeuCau.getRequestDispatcher("/jsp/pos/hoa-don.jsp").forward(yeuCau, phanHoi);
    }

    private LocalDate docNgay(String chuoi, LocalDate macDinh) {
        if (chuoi.isEmpty()) {
            return macDinh;
        }
        try {
            return LocalDate.parse(chuoi);
        } catch (DateTimeParseException loi) {
            return macDinh;
        }
    }
}
