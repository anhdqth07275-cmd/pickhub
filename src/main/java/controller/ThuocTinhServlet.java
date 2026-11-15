package controller;

import dto.ThuocTinhDTO;
import entity.*;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Thuoc tinh san pham: /wp-admin/thuoc-tinh?loai=mausac | chat-lieu | khoi-luong | chu-vi  (module M1).
 * 1 servlet cho 4 bang: MauSac, ChatLieu, KhoiLuong, ChuViTayCam (phan biet bang tham so "loai").
 * Da lam: danh sach, them nhanh, ngung / kich hoat.  TODO: sua gia tri.
 */
@WebServlet("/wp-admin/thuoc-tinh")
public class ThuocTinhServlet extends BaseServlet {

    private final MauSacRepository mauSacRepo = new MauSacRepository();
    private final ChatLieuRepository chatLieuRepo = new ChatLieuRepository();
    private final KhoiLuongRepository khoiLuongRepo = new KhoiLuongRepository();
    private final ChuViTayCamRepository chuViRepo = new ChuViTayCamRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loai = docLoai(req);
        req.setAttribute("loai", loai);
        req.setAttribute("tenThuocTinh", tenThuocTinh(loai));
        req.setAttribute("dsThuocTinh", layDanhSach(loai));
        hienThi(req, resp, "san-pham/thuoc-tinh", "thuoc-tinh", "Thuộc tính sản phẩm");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String loai = docLoai(req);
        String duongDan = "/wp-admin/thuoc-tinh?loai=" + loai;
        try {
            if ("them".equals(docChuoi(req, "hanhDong"))) {
                them(loai, docChuoi(req, "giaTri"));
                chuyenHuong(req, resp, duongDan, "Đã thêm " + tenThuocTinh(loai).toLowerCase());
            } else {
                doiTrangThai(loai, docLong(req, "id"));
                chuyenHuong(req, resp, duongDan, "Đã cập nhật trạng thái");
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, duongDan, e.getMessage());
        }
    }

    // loai hop le, sai thi mac dinh mau sac
    private String docLoai(HttpServletRequest req) {
        String loai = docChuoi(req, "loai");
        return (loai.equals("chat-lieu") || loai.equals("khoi-luong") || loai.equals("chu-vi")) ? loai : "mausac";
    }

    private String tenThuocTinh(String loai) {
        switch (loai) {
            case "chat-lieu": return "Chất liệu";
            case "khoi-luong": return "Khối lượng";
            case "chu-vi": return "Chu vi tay cầm";
            default: return "Màu sắc";
        }
    }

    // note code nhu nay nhe ban: gom 4 bang khac nhau ve cung 1 kieu ThuocTinhDTO de JSP chi can 1 bang hien thi
    private List<ThuocTinhDTO> layDanhSach(String loai) {
        List<ThuocTinhDTO> ds = new ArrayList<ThuocTinhDTO>();
        switch (loai) {
            case "chat-lieu":
                for (ChatLieu c : chatLieuRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(c.getMaChatLieu(), c.getTenChatLieu(), c.getTrangThai(), c.getNgayTao()));
                break;
            case "khoi-luong":
                for (KhoiLuong k : khoiLuongRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(k.getMaKhoiLuong(), k.getKhoiLuongGram() + " g", k.getTrangThai(), k.getNgayTao()));
                break;
            case "chu-vi":
                for (ChuViTayCam c : chuViRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(c.getMaChuViTayCam(), c.getChuViInch().toPlainString() + " inch", c.getTrangThai(), c.getNgayTao()));
                break;
            default:
                for (MauSac m : mauSacRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(m.getMaMauSac(), m.getTenMauSac(), m.getTrangThai(), m.getNgayTao()));
        }
        return ds;
    }

    // note code nhu nay nhe ban: validate theo CHECK / UNIQUE trong SQL (ten khong trung, so do > 0 va khong trung)
    private void them(String loai, String giaTri) {
        if (giaTri.isEmpty()) throw new NghiepVuException("Vui lòng nhập giá trị");
        switch (loai) {
            case "chat-lieu": {
                if (giaTri.length() > 100) throw new NghiepVuException("Tên chất liệu tối đa 100 ký tự");
                if (chatLieuRepo.daTonTai("tenChatLieu", giaTri, null)) throw new NghiepVuException("Chất liệu đã tồn tại");
                ChatLieu c = new ChatLieu();
                c.setTenChatLieu(giaTri);
                c.setTrangThai("HOAT_DONG");
                chatLieuRepo.luu(c);
                break;
            }
            case "khoi-luong": {
                int gram = soNguyenDuong(giaTri, 32767, "Khối lượng phải là số nguyên dương (gram)");
                if (khoiLuongRepo.daTonTai("khoiLuongGram", gram, null)) throw new NghiepVuException("Khối lượng đã tồn tại");
                KhoiLuong k = new KhoiLuong();
                k.setKhoiLuongGram(gram);
                k.setTrangThai("HOAT_DONG");
                khoiLuongRepo.luu(k);
                break;
            }
            case "chu-vi": {
                BigDecimal inch;
                try {
                    inch = new BigDecimal(giaTri.replace(',', '.')).setScale(3, BigDecimal.ROUND_HALF_UP);   // DB giu 3 so thap phan
                } catch (NumberFormatException e) {
                    throw new NghiepVuException("Chu vi phải là số (inch), ví dụ 4.125");
                }
                if (inch.signum() <= 0 || inch.compareTo(new BigDecimal("10")) >= 0) throw new NghiepVuException("Chu vi phải lớn hơn 0 và nhỏ hơn 10 inch");
                if (chuViRepo.daTonTai("chuViInch", inch, null)) throw new NghiepVuException("Chu vi đã tồn tại");
                ChuViTayCam c = new ChuViTayCam();
                c.setChuViInch(inch);
                c.setTrangThai("HOAT_DONG");
                chuViRepo.luu(c);
                break;
            }
            default: {
                if (giaTri.length() > 50) throw new NghiepVuException("Tên màu tối đa 50 ký tự");
                if (mauSacRepo.daTonTai("tenMauSac", giaTri, null)) throw new NghiepVuException("Màu sắc đã tồn tại");
                MauSac m = new MauSac();
                m.setTenMauSac(giaTri);
                m.setTrangThai("HOAT_DONG");
                mauSacRepo.luu(m);
            }
        }
    }

    private int soNguyenDuong(String chuoi, int toiDa, String thongBaoLoi) {
        try {
            int so = Integer.parseInt(chuoi);
            if (so <= 0 || so > toiDa) throw new NghiepVuException(thongBaoLoi);
            return so;
        } catch (NumberFormatException e) {
            throw new NghiepVuException(thongBaoLoi);
        }
    }

    // note code nhu nay nhe ban: "ngung su dung thay vi xoa" - HOAT_DONG <-> NGUNG
    private void doiTrangThai(String loai, Long id) {
        if (id == null) throw new NghiepVuException("Thiếu mã thuộc tính");
        switch (loai) {
            case "chat-lieu": {
                ChatLieu c = chatLieuRepo.timTheoId(id);
                if (c != null) { c.setTrangThai(lat(c.getTrangThai())); chatLieuRepo.capNhat(c); }
                break;
            }
            case "khoi-luong": {
                KhoiLuong k = khoiLuongRepo.timTheoId(id);
                if (k != null) { k.setTrangThai(lat(k.getTrangThai())); khoiLuongRepo.capNhat(k); }
                break;
            }
            case "chu-vi": {
                ChuViTayCam c = chuViRepo.timTheoId(id);
                if (c != null) { c.setTrangThai(lat(c.getTrangThai())); chuViRepo.capNhat(c); }
                break;
            }
            default: {
                MauSac m = mauSacRepo.timTheoId(id);
                if (m != null) { m.setTrangThai(lat(m.getTrangThai())); mauSacRepo.capNhat(m); }
            }
        }
    }

    private String lat(String trangThai) {
        return "HOAT_DONG".equals(trangThai) ? "NGUNG" : "HOAT_DONG";
    }
}
