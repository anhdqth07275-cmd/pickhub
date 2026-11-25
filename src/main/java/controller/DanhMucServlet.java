package controller;

import dto.BoLoc;
import entity.DanhMuc;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.DanhMucRepository;
import service.DanhMucService;

import java.io.IOException;
import java.util.List;

/**
 * Danh muc: /wp-admin/danh-muc  (module M1).
 * Bang lien quan: DanhMuc (tu tham chieu cha - con: Vot -> Carbon / Fiberglass / Graphite).
 * Da lam: danh sach + ngung / kich hoat + THEM / SUA (form) + XOA  => du CRUD.
 * Luat nghiep vu (chan chon chinh no lam cha, chan vong lap cha-con, chan trung (cha, ten), chi xoa khi danh muc trong) nam o DanhMucService.
 */
@WebServlet("/wp-admin/danh-muc")
public class DanhMucServlet extends BaseServlet {

    private final DanhMucRepository repo = new DanhMucRepository();
    private final DanhMucService service = new DanhMucService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // note code nhu nay nhe ban: ?hanhDong=form la THEM MOI, ?hanhDong=form&id=5 la SUA danh muc so 5 -> hien form, con lai hien danh sach
        if ("form".equals(docChuoi(req, "hanhDong"))) {
            DanhMuc dm = repo.timTheoId(docLong(req, "id"));
            if (docLong(req, "id") != null && dm == null) {
                chuyenHuongLoi(req, resp, "/wp-admin/danh-muc", "Không tìm thấy danh mục");
                return;
            }
            hienForm(req, resp, dm == null ? new DanhMuc() : dm);
            return;
        }
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");
        if (hanhDong.equals("luu")) {          // THEM MOI hoac SUA (id rong = them moi)
            luu(req, resp);
            return;
        }
        if (hanhDong.equals("xoa")) {          // XOA
            try {
                service.xoa(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/danh-muc", "Đã xóa danh mục");
            } catch (NghiepVuException e) {
                chuyenHuongLoi(req, resp, "/wp-admin/danh-muc", e.getMessage());
            }
            return;
        }
        DanhMuc dm = repo.timTheoId(docLong(req, "id"));
        if (dm != null && "doi-trang-thai".equals(docChuoi(req, "hanhDong"))) {
            dm.setTrangThai("HOAT_DONG".equals(dm.getTrangThai()) ? "NGUNG" : "HOAT_DONG");
            repo.capNhat(dm);
            chuyenHuong(req, resp, "/wp-admin/danh-muc", "Đã cập nhật trạng thái danh mục");
            return;
        }
        chuyenHuongLoi(req, resp, "/wp-admin/danh-muc", "Không tìm thấy danh mục");
    }

    // ---------------- THEM / SUA ----------------

    private void luu(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = docLong(req, "id");
        Long maCha = docLong(req, "maDanhMucCha");
        try {
            service.luu(id, docChuoi(req, "tenDanhMuc"), maCha, docChuoi(req, "moTa"));
            chuyenHuong(req, resp, "/wp-admin/danh-muc", id == null ? "Đã thêm danh mục" : "Đã cập nhật danh mục");
        } catch (NghiepVuException e) {
            // note code nhu nay nhe ban: loi thi HIEN LAI FORM kem du lieu vua nhap (khong redirect de nguoi dung khong phai go lai)
            DanhMuc dm = new DanhMuc();
            dm.setMaDanhMuc(id);
            dm.setTenDanhMuc(docChuoi(req, "tenDanhMuc"));
            dm.setMoTa(docChuoi(req, "moTa"));
            dm.setDanhMucCha(repo.timTheoId(maCha));        // chi de form chon lai dung danh muc cha, khong luu gi o day
            req.setAttribute("loi", e.getMessage());
            hienForm(req, resp, dm);
        }
    }

    /** Dua danh muc + danh sach "danh muc cha" cho danh-muc-form.jsp. */
    private void hienForm(HttpServletRequest req, HttpServletResponse resp, DanhMuc dm) throws ServletException, IOException {
        List<DanhMuc> dsCha = service.layDanhMucChaCoTheChon(dm.getMaDanhMuc());
        // note code nhu nay nhe ban: cha hien tai co the dang NGUNG (khong nam trong danh sach chon duoc) -> them vao dau danh sach
        // de o chon van hien dung cha cu, khong bi nhay ve "Khong co" roi luu nham thanh danh muc goc
        DanhMuc chaHienTai = dm.getDanhMucCha();
        if (chaHienTai != null) {
            boolean daCo = false;
            for (DanhMuc c : dsCha) if (c.getMaDanhMuc().equals(chaHienTai.getMaDanhMuc())) daCo = true;
            if (!daCo) dsCha.add(0, chaHienTai);
        }
        req.setAttribute("dm", dm);
        req.setAttribute("dsCha", dsCha);
        hienThi(req, resp, "san-pham/danh-muc-form", "danh-muc", dm.getMaDanhMuc() == null ? "Thêm danh mục" : "Sửa danh mục");
    }
}
