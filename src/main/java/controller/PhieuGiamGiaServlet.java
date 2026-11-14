package controller;

import dto.BoLoc;
import entity.PhieuGiamGia;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.PhieuGiamGiaRepository;

import java.io.IOException;

/**
 * Phieu giam gia: /wp-admin/phieu-giam-gia  (module M4). Chi QUAN_LY, QUAN_TRI.
 * Bang lien quan: PhieuGiamGia. Da lam: danh sach, tim kiem, tam dung / chay lai.
 * TODO: form them / sua (maGiamGia <= 30 ky tu, in hoa, khong trung; giamToiDa chi dung cho PHAN_TRAM; gioiHanMoiNguoi >= 1).
 */
@WebServlet("/wp-admin/phieu-giam-gia")
public class PhieuGiamGiaServlet extends BaseServlet {

    private final PhieuGiamGiaRepository repo = new PhieuGiamGiaRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BoLoc loc = new BoLoc()
                .cotTim("maGiamGia,tenPhieu")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        req.setAttribute("pt", repo.timKiem(loc));
        req.setAttribute("duongDan", "/wp-admin/phieu-giam-gia");
        hienThi(req, resp, "phieu-giam-gia/danh-sach", "phieu-giam-gia", "Phiếu giảm giá");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PhieuGiamGia p = repo.timTheoId(docLong(req, "id"));
        if (p == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/phieu-giam-gia", "Không tìm thấy phiếu giảm giá");
            return;
        }
        // note code nhu nay nhe ban: chi cho doi qua lai DANG_CHAY <-> TAM_DUNG; SAP_CHAY / HET_HAN do thoi gian quyet dinh (scheduler)
        if ("DANG_CHAY".equals(p.getTrangThai())) {
            p.setTrangThai("TAM_DUNG");
        } else if ("TAM_DUNG".equals(p.getTrangThai())) {
            p.setTrangThai("DANG_CHAY");
        } else {
            chuyenHuongLoi(req, resp, "/wp-admin/phieu-giam-gia", "Chỉ tạm dừng / chạy lại được phiếu đang chạy");
            return;
        }
        repo.capNhat(p);
        chuyenHuong(req, resp, "/wp-admin/phieu-giam-gia", "Đã cập nhật trạng thái phiếu " + p.getMaGiamGia());
    }
}
