package com.pickhub.controller;

import com.pickhub.dto.PhieuKhaDungDTO;
import com.pickhub.entity.PhieuGiamGia;
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
import java.util.List;

/**
 * Popup "Xem va chon voucher" cua POS: GET /pos/api/phieu-giam-gia/danh-sach?tamTinh=...
 * Tra ve cac voucher dang dung duoc + so tien giam uoc tinh tren hoa don hien tai (chi XEM, chua ghi nhan luot dung).
 * Duoc PosFilter bao ve (/pos/*) nen chua dang nhap se nhan HTTP 401 dang JSON.
 */
@WebServlet("/pos/api/phieu-giam-gia/danh-sach")
public class PosPhieuDanhSachApiServlet extends HttpServlet {

    private final IPhieuGiamGiaService phieuGiamGiaService = new PhieuGiamGiaServiceImpl();

    @Override
    protected void doGet(HttpServletRequest yeuCau, HttpServletResponse phanHoi) throws IOException {
        try {
            BigDecimal tamTinh = DinhDangUtil.docTien(ServletUtil.docChuoi(yeuCau, "tamTinh"), "Tạm tính");
            if (tamTinh == null) {
                tamTinh = BigDecimal.ZERO;
            }
            List<PhieuKhaDungDTO> danhSach = phieuGiamGiaService.layDanhSachKhaDung(tamTinh);

            // note code nhu nay nhe ban: tu ghep JSON bang JsonUtil (cung cach voi PosPhieuApiServlet), moi chuoi deu qua JsonUtil.chuoi de thoat ky tu dac biet
            StringBuilder json = new StringBuilder("{\"ok\":true,\"ds\":[");
            for (int i = 0; i < danhSach.size(); i++) {
                PhieuKhaDungDTO d = danhSach.get(i);
                PhieuGiamGia p = d.getPhieu();
                if (i > 0) {
                    json.append(',');
                }
                json.append("{\"ma\":").append(JsonUtil.chuoi(p.getMaGiamGia()))
                        .append(",\"ten\":").append(JsonUtil.chuoi(p.getTenPhieu()))
                        .append(",\"loai\":").append(JsonUtil.chuoi(p.getLoaiGiam()))
                        .append(",\"giaTri\":").append(JsonUtil.so(p.getGiaTriGiam()))
                        .append(",\"giamToiDa\":").append(JsonUtil.so(p.getGiamToiDa()))
                        .append(",\"donToiThieu\":").append(JsonUtil.so(p.getGiaTriDonToiThieu()))
                        .append(",\"hanDung\":").append(JsonUtil.chuoi(DinhDangUtil.ngayGio(p.getNgayKetThuc())))
                        .append(",\"moiNguoi\":").append(p.getGioiHanMoiNguoi() == null ? "null" : String.valueOf(p.getGioiHanMoiNguoi()))
                        // conLuot = null nghia la khong gioi han tong luot
                        .append(",\"conLuot\":").append(p.getGioiHanSuDung() == null ? "null"
                                : String.valueOf(Math.max(0, p.getGioiHanSuDung() - p.getSoLanDaSuDung())))
                        .append(",\"duDieuKien\":").append(d.isDuDieuKien())
                        .append(",\"soTienGiam\":").append(JsonUtil.so(d.getSoTienGiam()))
                        .append(",\"canThem\":").append(JsonUtil.so(d.getCanThemTien()))
                        .append('}');
            }
            json.append("]}");
            ServletUtil.guiJson(phanHoi, 200, json.toString());
        } catch (NghiepVuException loi) {
            ServletUtil.guiJson(phanHoi, 200, JsonUtil.loi(loi.getMessage()));
        } catch (RuntimeException loi) {
            loi.printStackTrace();
            ServletUtil.guiJson(phanHoi, 500, JsonUtil.loi("Lỗi hệ thống khi tải danh sách voucher"));
        }
    }
}
