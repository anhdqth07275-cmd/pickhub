package com.pickhub.controller;

import com.pickhub.entity.NhanVien;
import com.pickhub.service.IHoaDonService;
import com.pickhub.service.ISanPhamService;
import com.pickhub.service.impl.HoaDonServiceImpl;
import com.pickhub.service.impl.SanPhamServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/** Man hinh ban hang tai quay (POS): /pos */
@WebServlet("/pos")
public class PosServlet extends HttpServlet {

    private final ISanPhamService sanPhamService = new SanPhamServiceImpl();
    private final IHoaDonService hoaDonService = new HoaDonServiceImpl();

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws ServletException, IOException {
        NhanVien nhanVien = (NhanVien) yeuCau.getSession().getAttribute("nhanVien");
        yeuCau.setAttribute("tuyChon", sanPhamService.layTuyChonBoLoc());           // du lieu cho thanh bo loc
        LocalDate homNay = LocalDate.now();
        yeuCau.setAttribute("tongKet", hoaDonService.tongKet(homNay, homNay, nhanVien.getMaNhanVien()));   // ca ban hom nay cua nhan vien nay
        yeuCau.getRequestDispatcher("/jsp/pos/ban-hang.jsp").forward(yeuCau, phanHoi);
    }
}
