package com.pickhub.controller;

import com.pickhub.dto.BienTheBanDTO;
import com.pickhub.dto.BoLocBienTheDTO;
import com.pickhub.service.ISanPhamService;
import com.pickhub.service.impl.SanPhamServiceImpl;
import com.pickhub.util.DinhDangUtil;
import com.pickhub.util.JsonUtil;
import com.pickhub.util.NghiepVuException;
import com.pickhub.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * API tim san pham cho man hinh ban hang (tra JSON): GET /pos/api/san-pham
 * Tham so loc: tuKhoa, danhMuc, thuongHieu, loai, mau, khoiLuong, chatLieu, chuVi, giaTu, giaDen, ton, km, sapXep, trang
 * Them nhom=1: tra theo SAN PHAM (the san pham + cac bien the) thay vi theo tung bien the.
 * Rieng ?sku=MA_HANG: tim chinh xac 1 mat hang (dung khi quet ma vach / go ma roi Enter).
 */
@WebServlet("/pos/api/san-pham")
public class PosSanPhamApiServlet extends HttpServlet {

    private final ISanPhamService sanPhamService = new SanPhamServiceImpl();

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws IOException {
        try {
            String sku = ServletUtil.docChuoi(yeuCau, "sku");
            if (!sku.isEmpty()) {
                BienTheBanDTO matHang = sanPhamService.timTheoMaHang(sku);
                if (matHang == null) {
                    ServletUtil.guiJson(phanHoi, 200, JsonUtil.loi("Không tìm thấy mã hàng \"" + sku + "\""));
                } else {
                    ServletUtil.guiJson(phanHoi, 200, "{\"ok\":true,\"item\":" + matHang.toJson() + "}");
                }
                return;
            }

            BoLocBienTheDTO boLoc = new BoLocBienTheDTO();
            boLoc.setTuKhoa(ServletUtil.docChuoi(yeuCau, "tuKhoa"));
            boLoc.setMaDanhMuc(ServletUtil.docLong(yeuCau, "danhMuc"));
            boLoc.setMaThuongHieu(ServletUtil.docLong(yeuCau, "thuongHieu"));
            boLoc.setLoaiSanPham(ServletUtil.docChuoi(yeuCau, "loai"));
            boLoc.setMaMauSac(ServletUtil.docLong(yeuCau, "mau"));
            boLoc.setMaKhoiLuong(ServletUtil.docLong(yeuCau, "khoiLuong"));
            boLoc.setMaChatLieu(ServletUtil.docLong(yeuCau, "chatLieu"));
            boLoc.setMaChuViTayCam(ServletUtil.docLong(yeuCau, "chuVi"));
            boLoc.setGiaTu(DinhDangUtil.docTien(ServletUtil.docChuoi(yeuCau, "giaTu"), "Giá từ"));
            boLoc.setGiaDen(DinhDangUtil.docTien(ServletUtil.docChuoi(yeuCau, "giaDen"), "Giá đến"));
            String ton = ServletUtil.docChuoi(yeuCau, "ton");
            if (!ton.isEmpty()) {
                boLoc.setTinhTrangTon(ton);
            }
            boLoc.setGoiYKhuyenMai("1".equals(ServletUtil.docChuoi(yeuCau, "km")));
            String sapXep = ServletUtil.docChuoi(yeuCau, "sapXep");
            if (!sapXep.isEmpty()) {
                boLoc.setSapXep(sapXep);
            }
            boLoc.setTrang(ServletUtil.docSo(yeuCau, "trang", 1));
            // note code nhu nay nhe ban: ?nhom=1 -> tra danh sach THEO SAN PHAM (moi san pham 1 the kem cac bien the, cho popup chon bien the);
            // khong co ?nhom=1 -> van tra theo bien the nhu cu
            if ("1".equals(ServletUtil.docChuoi(yeuCau, "nhom"))) {
                ServletUtil.guiJson(phanHoi, 200, sanPhamService.timKiemSanPham(boLoc).toJson());
            } else {
                ServletUtil.guiJson(phanHoi, 200, sanPhamService.timKiemBienThe(boLoc).toJson());
            }
        } catch (NghiepVuException loi) {
            ServletUtil.guiJson(phanHoi, 200, JsonUtil.loi(loi.getMessage()));
        } catch (RuntimeException loi) {
            loi.printStackTrace();
            ServletUtil.guiJson(phanHoi, 500, JsonUtil.loi("Lỗi hệ thống khi tìm sản phẩm"));
        }
    }
}
