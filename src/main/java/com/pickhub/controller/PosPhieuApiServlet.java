package com.pickhub.controller;

import com.pickhub.dto.KetQuaPhieuDTO;
import com.pickhub.service.IPhieuGiamGiaService;
import com.pickhub.service.impl.PhieuGiamGiaServiceImpl;
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

/** Kiem tra ma giam gia tren hoa don dang lap: GET /pos/api/phieu-giam-gia?ma=...&tamTinh=... (chi XEM TRUOC, chua ghi nhan luot dung) */
@WebServlet("/pos/api/phieu-giam-gia")
public class PosPhieuApiServlet extends HttpServlet {

    private final IPhieuGiamGiaService phieuGiamGiaService = new PhieuGiamGiaServiceImpl();

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws IOException {
        try {
            BigDecimal tamTinh = DinhDangUtil.docTien(ServletUtil.docChuoi(yeuCau, "tamTinh"), "Tạm tính");
            if (tamTinh == null) {
                tamTinh = BigDecimal.ZERO;
            }
            KetQuaPhieuDTO ketQua = phieuGiamGiaService.tinhGiam(ServletUtil.docChuoi(yeuCau, "ma"), tamTinh);
            ServletUtil.guiJson(phanHoi, 200, "{\"ok\":true,\"ma\":" + JsonUtil.chuoi(ketQua.getPhieu().getMaGiamGia())
                    + ",\"ten\":" + JsonUtil.chuoi(ketQua.getPhieu().getTenPhieu())
                    + ",\"soTienGiam\":" + JsonUtil.so(ketQua.getSoTienGiam()) + "}");
        } catch (NghiepVuException loi) {
            ServletUtil.guiJson(phanHoi, 200, JsonUtil.loi(loi.getMessage()));
        } catch (RuntimeException loi) {
            loi.printStackTrace();
            ServletUtil.guiJson(phanHoi, 500, JsonUtil.loi("Lỗi hệ thống khi kiểm tra mã giảm giá"));
        }
    }
}
