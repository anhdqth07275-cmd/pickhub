package controller;

import dto.BoLoc;
import entity.DanhMuc;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.DanhMucRepository;

import java.io.IOException;

/**
 * Danh muc: /wp-admin/danh-muc  (module M1).
 * Bang lien quan: DanhMuc (tu tham chieu cha - con: Vot -> Carbon / Fiberglass / Graphite).
 * Da lam: danh sach + ngung / kich hoat.
 * TODO: form them / sua theo mau ThuongHieuServlet. Luu y: chan chon chinh no lam cha, chan vong lap cha-con, chan trung (cha, ten).
 */
@WebServlet("/wp-admin/danh-muc")
public class DanhMucServlet extends BaseServlet {

    private final DanhMucRepository repo = new DanhMucRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTim("tenDanhMuc")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .sapXep("x.maDanhMuc asc")
                .trang(docSo(req, "trang", 1))
                .kichThuoc(20);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/danh-muc");
        hienThi(req, resp, "san-pham/danh-muc", "danh-muc", "Danh mục");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DanhMuc dm = repo.timTheoId(docLong(req, "id"));
        if (dm != null && "doi-trang-thai".equals(docChuoi(req, "hanhDong"))) {
            dm.setTrangThai("HOAT_DONG".equals(dm.getTrangThai()) ? "NGUNG" : "HOAT_DONG");
            repo.capNhat(dm);
            chuyenHuong(req, resp, "/wp-admin/danh-muc", "Đã cập nhật trạng thái danh mục");
            return;
        }
        chuyenHuongLoi(req, resp, "/wp-admin/danh-muc", "Không tìm thấy danh mục");
    }
}
