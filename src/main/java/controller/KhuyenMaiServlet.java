package controller;

import dto.BoLoc;
import entity.ChuongTrinhKhuyenMai;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ChuongTrinhKhuyenMaiRepository;
import service.KhuyenMaiService;
import util.NhapLieuUtil;

import java.io.IOException;

/**
 * Chuong trinh khuyen mai: /wp-admin/khuyen-mai  (module M4). Chi QUAN_LY, QUAN_TRI.
 * Bang lien quan: ChuongTrinhKhuyenMai.
 *   GET                          -> danh sach (tim kiem + loc trang thai + phan trang)
 *   GET  ?hanhDong=form[&id=]    -> form them / sua
 *   POST hanhDong=luu            -> them hoac sua (validate trong KhuyenMaiService)
 *   POST hanhDong=doi-trang-thai -> tam dung / chay lai
 * Trang thai SAP_CHAY -> DANG_CHAY -> KET_THUC do KhuyenMaiScheduler tu doi theo thoi gian.
 */
@WebServlet("/wp-admin/khuyen-mai")
public class KhuyenMaiServlet extends BaseServlet {

    private final ChuongTrinhKhuyenMaiRepository repo = new ChuongTrinhKhuyenMaiRepository();
    private final KhuyenMaiService service = new KhuyenMaiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("form".equals(docChuoi(req, "hanhDong"))) {
            Long id = docLong(req, "id");
            ChuongTrinhKhuyenMai ct = id == null ? new ChuongTrinhKhuyenMai() : repo.timTheoId(id);
            if (ct == null) {
                chuyenHuongLoi(req, resp, "/wp-admin/khuyen-mai", "Không tìm thấy chương trình khuyến mãi");
                return;
            }
            req.setAttribute("ct", ct);
            hienThi(req, resp, "khuyen-mai/form", "khuyen-mai", id == null ? "Thêm chương trình khuyến mãi" : "Sửa chương trình khuyến mãi");
            return;
        }

        BoLoc loc = new BoLoc()
                .cotTim("tenChuongTrinh")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/khuyen-mai");
        hienThi(req, resp, "khuyen-mai/danh-sach", "khuyen-mai", "Chương trình khuyến mãi");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");

        // ---- tam dung / chay lai ----
        if ("doi-trang-thai".equals(hanhDong)) {
            try {
                service.doiTrangThai(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/khuyen-mai", "Đã cập nhật trạng thái chương trình");
            } catch (NghiepVuException e) {
                chuyenHuongLoi(req, resp, "/wp-admin/khuyen-mai", e.getMessage());
            }
            return;
        }

        // ---- luu (them / sua) ----
        Long id = docLong(req, "id");
        ChuongTrinhKhuyenMai ct = id == null ? new ChuongTrinhKhuyenMai() : repo.timTheoId(id);
        if (ct == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/khuyen-mai", "Không tìm thấy chương trình khuyến mãi");
            return;
        }
        try {
            ct.setTenChuongTrinh(docChuoi(req, "tenChuongTrinh"));
            ct.setMoTa(docChuoi(req, "moTa").isEmpty() ? null : docChuoi(req, "moTa"));
            ct.setNgayBatDau(NhapLieuUtil.docThoiGian(docChuoi(req, "ngayBatDau"), "Ngày bắt đầu"));
            ct.setNgayKetThuc(NhapLieuUtil.docThoiGian(docChuoi(req, "ngayKetThuc"), "Ngày kết thúc"));

            // note code nhu nay nhe ban: maNguoiTao = nhan vien dang dang nhap (lay tu session), chi luu khi them moi
            service.luuChuongTrinh(ct, taiKhoan(req).getId());
            chuyenHuong(req, resp, "/wp-admin/khuyen-mai", "Đã lưu chương trình khuyến mãi");
        } catch (NghiepVuException e) {
            // loi validate: hien lai form kem du lieu vua nhap
            req.setAttribute("ct", ct);
            req.setAttribute("loi", e.getMessage());
            hienThi(req, resp, "khuyen-mai/form", "khuyen-mai", id == null ? "Thêm chương trình khuyến mãi" : "Sửa chương trình khuyến mãi");
        }
    }
}
