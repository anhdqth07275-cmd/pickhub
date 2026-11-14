package controller;

import dto.BoLoc;
import dto.KetQuaPhieuDTO;
import entity.PhieuGiamGia;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.PhieuGiamGiaRepository;
import service.PhieuGiamGiaService;
import util.NhapLieuUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Phieu giam gia: /wp-admin/phieu-giam-gia  (module M4). Chi QUAN_LY, QUAN_TRI.
 * Bang lien quan: PhieuGiamGia.
 *   GET                          -> danh sach + o "Thu phieu" (goi kiemTraVaTinhGiam de kiem tra nhanh)
 *   GET  ?hanhDong=form[&id=]    -> form them / sua
 *   POST hanhDong=luu            -> them hoac sua (validate trong PhieuGiamGiaService)
 *   POST hanhDong=doi-trang-thai -> tam dung / chay lai
 */
@WebServlet("/wp-admin/phieu-giam-gia")
public class PhieuGiamGiaServlet extends BaseServlet {

    private final PhieuGiamGiaRepository repo = new PhieuGiamGiaRepository();
    private final PhieuGiamGiaService service = new PhieuGiamGiaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("form".equals(docChuoi(req, "hanhDong"))) {
            Long id = docLong(req, "id");
            PhieuGiamGia p;
            if (id == null) {
                p = new PhieuGiamGia();          // gia tri mac dinh cho form them moi
                p.setLoaiGiam("PHAN_TRAM");
                p.setGioiHanMoiNguoi(1);
                p.setGiaTriDonToiThieu(BigDecimal.ZERO);
            } else {
                p = repo.timTheoId(id);
            }
            if (p == null) {
                chuyenHuongLoi(req, resp, "/wp-admin/phieu-giam-gia", "Không tìm thấy phiếu giảm giá");
                return;
            }
            req.setAttribute("p", p);
            hienThi(req, resp, "phieu-giam-gia/form", "phieu-giam-gia", id == null ? "Thêm phiếu giảm giá" : "Sửa phiếu giảm giá");
            return;
        }

        // ---- o "Thu phieu": nhap ma + (ma khach) + tam tinh, he thong chay dung ham ma M5 / M6 se goi ----
        String thuMa = docChuoi(req, "thuMa");
        if (!thuMa.isEmpty()) {
            try {
                BigDecimal tamTinh = NhapLieuUtil.docSo(docChuoi(req, "thuTamTinh"), "Tạm tính");
                req.setAttribute("ketQuaThu", service.kiemTraVaTinhGiam(thuMa, docLong(req, "thuKhach"), tamTinh));
            } catch (NghiepVuException e) {
                req.setAttribute("ketQuaThu", KetQuaPhieuDTO.tuChoi(e.getMessage()));
            }
        }

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String hanhDong = docChuoi(req, "hanhDong");

        // ---- tam dung / chay lai ----
        if ("doi-trang-thai".equals(hanhDong)) {
            try {
                service.doiTrangThai(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/phieu-giam-gia", "Đã cập nhật trạng thái phiếu giảm giá");
            } catch (NghiepVuException e) {
                chuyenHuongLoi(req, resp, "/wp-admin/phieu-giam-gia", e.getMessage());
            }
            return;
        }

        // ---- luu (them / sua) ----
        Long id = docLong(req, "id");
        PhieuGiamGia p = id == null ? new PhieuGiamGia() : repo.timTheoId(id);
        if (p == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/phieu-giam-gia", "Không tìm thấy phiếu giảm giá");
            return;
        }
        try {
            p.setMaGiamGia(docChuoi(req, "maGiamGia"));
            p.setTenPhieu(docChuoi(req, "tenPhieu"));
            p.setLoaiGiam(docChuoi(req, "loaiGiam"));
            p.setGiaTriGiam(NhapLieuUtil.docSo(docChuoi(req, "giaTriGiam"), "Giá trị giảm"));
            p.setGiamToiDa(NhapLieuUtil.docSo(docChuoi(req, "giamToiDa"), "Mức giảm tối đa"));
            p.setGiaTriDonToiThieu(NhapLieuUtil.docSo(docChuoi(req, "giaTriDonToiThieu"), "Giá trị đơn tối thiểu"));
            p.setGioiHanSuDung(NhapLieuUtil.docSoNguyen(docChuoi(req, "gioiHanSuDung"), "Giới hạn tổng lượt dùng"));   // trong = khong gioi han
            p.setGioiHanMoiNguoi(NhapLieuUtil.docSoNguyen(docChuoi(req, "gioiHanMoiNguoi"), "Giới hạn mỗi người"));
            p.setNgayBatDau(NhapLieuUtil.docThoiGian(docChuoi(req, "ngayBatDau"), "Ngày bắt đầu"));
            p.setNgayKetThuc(NhapLieuUtil.docThoiGian(docChuoi(req, "ngayKetThuc"), "Ngày kết thúc"));

            service.luuPhieu(p);
            chuyenHuong(req, resp, "/wp-admin/phieu-giam-gia", "Đã lưu phiếu giảm giá");
        } catch (NghiepVuException e) {
            // loi validate: hien lai form kem du lieu vua nhap
            req.setAttribute("p", p);
            req.setAttribute("loi", e.getMessage());
            hienThi(req, resp, "phieu-giam-gia/form", "phieu-giam-gia", id == null ? "Thêm phiếu giảm giá" : "Sửa phiếu giảm giá");
        }
    }
}
