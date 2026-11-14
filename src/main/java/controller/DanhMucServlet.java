package controller;

import dto.BoLoc;
import dto.PhanTrang;
import entity.DanhMuc;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.DanhMucRepository;
import service.DanhMucService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Danh muc: /wp-admin/danh-muc  (module M2).
 * Bang lien quan: DanhMuc (tu tham chieu cha - con: Vot -> Carbon / Fiberglass / Graphite).
 *   GET                      -> danh sach dang cay (cha dung truoc, con thut vao ben duoi) + tim kiem + loc + phan trang
 *   GET ?hanhDong=form[&id=] -> form them / sua
 *   POST hanhDong=luu        -> them hoac sua (validate nam trong DanhMucService)
 *   POST hanhDong=doi-trang-thai -> ngung / kich hoat (khong xoa cung)
 */
@WebServlet("/wp-admin/danh-muc")
public class DanhMucServlet extends BaseServlet {

    private final DanhMucRepository repo = new DanhMucRepository();
    private final DanhMucService service = new DanhMucService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("form".equals(docChuoi(req, "hanhDong"))) {
            Long id = docLong(req, "id");
            DanhMuc dm = id == null ? new DanhMuc() : repo.timTheoId(id);
            if (dm == null) dm = new DanhMuc();
            hienThiForm(req, resp, dm, id);
            return;
        }

        // note code nhu nay nhe ban: sap xep de cha nam tren, con nam ngay duoi cha:
        //   khoa 1 = ma cua cha (danh muc goc thi lay ma cua chinh no) -> cha va con cung nhom
        //   khoa 2 = ma cha (goc la NULL nen luon dung truoc trong nhom)
        BoLoc loc = new BoLoc()
                .cotTim("tenDanhMuc")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .sapXep("coalesce(x.danhMucCha.maDanhMuc, x.maDanhMuc) asc, x.danhMucCha.maDanhMuc asc, x.tenDanhMuc asc")
                .trang(docSo(req, "trang", 1))
                .kichThuoc(20);
        PhanTrang<DanhMuc> pt = repo.timKiem(loc);

        // dem so san pham cua tung danh muc dang hien tren trang (toi da 20 dong nen khong nang)
        Map<Long, Long> soSanPham = new HashMap<Long, Long>();
        for (DanhMuc d : pt.getDanhSach()) {
            soSanPham.put(d.getMaDanhMuc(), service.demSanPham(d.getMaDanhMuc()));
        }
        req.setAttribute("pt", pt);
        req.setAttribute("soSanPham", soSanPham);
        req.setAttribute("duongDan", "/wp-admin/danh-muc");
        hienThi(req, resp, "san-pham/danh-muc", "danh-muc", "Danh mục");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");

        if ("doi-trang-thai".equals(hanhDong)) {
            try {
                service.doiTrangThai(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/danh-muc", "Đã cập nhật trạng thái danh mục");
            } catch (NghiepVuException e) {
                chuyenHuongLoi(req, resp, "/wp-admin/danh-muc", e.getMessage());
            }
            return;
        }

        // ---- luu (them / sua) ----
        Long id = docLong(req, "id");
        Long maCha = docLong(req, "maDanhMucCha");      // o chon "khong co cha" gui rong -> null -> danh muc goc
        try {
            service.luu(id, docChuoi(req, "tenDanhMuc"), maCha, docChuoi(req, "moTa"));
            chuyenHuong(req, resp, "/wp-admin/danh-muc", "Đã lưu danh mục");
        } catch (NghiepVuException e) {
            // loi validate: hien lai form kem du lieu vua nhap (chua luu vao DB)
            DanhMuc dm = new DanhMuc();
            dm.setMaDanhMuc(id);
            dm.setTenDanhMuc(docChuoi(req, "tenDanhMuc"));
            dm.setMoTa(docChuoi(req, "moTa"));
            if (maCha != null) {
                DanhMuc cha = new DanhMuc();
                cha.setMaDanhMuc(maCha);
                dm.setDanhMucCha(cha);
            }
            req.setAttribute("loi", e.getMessage());
            hienThiForm(req, resp, dm, id);
        }
    }

    private void hienThiForm(HttpServletRequest req, HttpServletResponse resp, DanhMuc dm, Long id) throws ServletException, IOException {
        req.setAttribute("dm", dm);
        req.setAttribute("dsCha", service.layDanhMucChaCoTheChon(id));     // dropdown cha: chi danh muc goc, bo chinh no
        hienThi(req, resp, "san-pham/danh-muc-form", "danh-muc", id == null ? "Thêm danh mục" : "Sửa danh mục");
    }
}
