package com.pickhub.controller;

import com.pickhub.dto.DongBanDTO;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.NhanVien;
import com.pickhub.service.IBanHangTaiQuayService;
import com.pickhub.service.impl.BanHangTaiQuayServiceImpl;
import com.pickhub.util.DinhDangUtil;
import com.pickhub.util.JsonUtil;
import com.pickhub.util.NghiepVuException;
import com.pickhub.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Thanh toan hoa don tai quay: POST /pos/api/thanh-toan
 * Tham so: dong ("maMatHang:soLuong,maMatHang:soLuong"), maGiamGia, phuongThuc, tienKhachDua, tenKhach, sdtKhach, ghiChu, maYeuCau.
 * Gia KHONG gui len - server tu tinh lai tu database.
 */
@WebServlet("/pos/api/thanh-toan")
public class PosThanhToanServlet extends HttpServlet {

    /** note code nhu nay nhe ban: nho cac maYeuCau da xu ly de neu nhan vien bam "Thanh toan" 2 lan (hoac mang lag gui lai) thi KHONG lap 2 hoa don. */
    private static final Set<String> DA_XU_LY = Collections.synchronizedSet(new HashSet<String>());

    private final IBanHangTaiQuayService banHangService = new BanHangTaiQuayServiceImpl();

    @Override
    protected void doPost(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws IOException {
        String maYeuCau = ServletUtil.docChuoi(yeuCau, "maYeuCau");
        if (!maYeuCau.isEmpty() && !DA_XU_LY.add(maYeuCau)) {
            ServletUtil.guiJson(phanHoi, 200, JsonUtil.loi("Hóa đơn này đã được thanh toán rồi, không gửi lại lần nữa"));
            return;
        }
        try {
            NhanVien nhanVien = (NhanVien) yeuCau.getSession().getAttribute("nhanVien");
            List<DongBanDTO> dong = docDanhSachDong(ServletUtil.docChuoi(yeuCau, "dong"));
            BigDecimal tienKhachDua = DinhDangUtil.docTien(ServletUtil.docChuoi(yeuCau, "tienKhachDua"), "Tiền khách đưa");

            HoaDon hoaDon = banHangService.thanhToan(nhanVien.getMaNhanVien(), dong, ServletUtil.docChuoi(yeuCau, "maGiamGia"),
                    ServletUtil.docChuoi(yeuCau, "phuongThuc"), tienKhachDua, ServletUtil.docChuoi(yeuCau, "tenKhach"),
                    ServletUtil.docChuoi(yeuCau, "sdtKhach"), ServletUtil.docChuoi(yeuCau, "ghiChu"));

            ServletUtil.guiJson(phanHoi, 200, "{\"ok\":true,\"maHoaDon\":" + hoaDon.getMaHoaDon()
                    + ",\"maTraCuu\":" + JsonUtil.chuoi(hoaDon.getMaTraCuu())
                    + ",\"tongTien\":" + JsonUtil.so(hoaDon.getTongTien())
                    + ",\"tienThua\":" + JsonUtil.so(hoaDon.getTienThua()) + "}");
        } catch (NghiepVuException loi) {
            DA_XU_LY.remove(maYeuCau);          // loi nghiep vu (het hang...) thi cho phep gui lai sau khi sua hoa don
            ServletUtil.guiJson(phanHoi, 200, JsonUtil.loi(loi.getMessage()));
        } catch (RuntimeException loi) {
            DA_XU_LY.remove(maYeuCau);
            loi.printStackTrace();
            ServletUtil.guiJson(phanHoi, 500, JsonUtil.loi("Lỗi hệ thống khi lưu hóa đơn, hóa đơn chưa được tạo. Vui lòng thử lại."));
        }
    }

    /** Doi chuoi "12:2,15:1" thanh danh sach dong. Sai dinh dang thi bao loi. */
    private List<DongBanDTO> docDanhSachDong(String chuoi) {
        List<DongBanDTO> ketQua = new ArrayList<DongBanDTO>();
        if (chuoi.isEmpty()) {
            return ketQua;
        }
        try {
            for (String phan : chuoi.split(",")) {
                String[] cap = phan.split(":");
                ketQua.add(new DongBanDTO(Long.valueOf(cap[0].trim()), Integer.parseInt(cap[1].trim())));
            }
        } catch (RuntimeException loi) {
            throw new NghiepVuException("Dữ liệu hóa đơn không hợp lệ");
        }
        return ketQua;
    }
}
