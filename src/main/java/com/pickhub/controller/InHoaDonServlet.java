package com.pickhub.controller;

import com.pickhub.entity.HoaDon;
import com.pickhub.service.IHoaDonService;
import com.pickhub.service.impl.HoaDonServiceImpl;
import com.pickhub.util.DinhDangUtil;
import com.pickhub.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Trang in hoa don (kho 80mm hoac A5): GET /pos/in-hoa-don?id=...&tuDong=1 (tuDong=1: mo la hien hop thoai in). */
@WebServlet("/pos/in-hoa-don")
public class InHoaDonServlet extends HttpServlet {

    private final IHoaDonService hoaDonService = new HoaDonServiceImpl();

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws ServletException, IOException {
        HoaDon hoaDon = hoaDonService.timHoaDonTaiQuayTheoId(ServletUtil.docLong(yeuCau, "id"));
        if (hoaDon == null) {
            phanHoi.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        yeuCau.setAttribute("hoaDon", hoaDon);
        yeuCau.setAttribute("dongHoaDon", hoaDonService.layDongHoaDon(hoaDon.getMaHoaDon()));
        yeuCau.setAttribute("thoiGian", DinhDangUtil.ngayGioGiay(hoaDon.getNgayDatHang()));
        yeuCau.setAttribute("phuongThuc", HoaDonServiceImpl.nhanPhuongThuc(hoaDon.getPhuongThucThanhToan()));
        yeuCau.setAttribute("tuDong", "1".equals(ServletUtil.docChuoi(yeuCau, "tuDong")));
        yeuCau.getRequestDispatcher("/jsp/pos/in-hoa-don.jsp").forward(yeuCau, phanHoi);
    }
}
