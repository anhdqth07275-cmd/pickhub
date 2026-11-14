package controller;

import entity.ChiTietChuongTrinhKhuyenMai;
import entity.ChiTietSanPham;
import entity.ChuongTrinhKhuyenMai;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ChiTietChuongTrinhKhuyenMaiRepository;
import repository.ChiTietSanPhamRepository;
import repository.ChuongTrinhKhuyenMaiRepository;
import service.KhuyenMaiService;
import util.NhapLieuUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Chi tiet 1 chuong trinh khuyen mai: /wp-admin/khuyen-mai/chi-tiet?id=...  (module M4)
 * Bang lien quan: ChuongTrinhKhuyenMai, ChiTietChuongTrinhKhuyenMai (danh sach chi tiet san pham duoc giam).
 *   GET [&suaDong=]   -> xem danh sach + form them (hoac form sua 1 dong)
 *   POST hanhDong=them | sua | go
 */
@WebServlet("/wp-admin/khuyen-mai/chi-tiet")
public class KhuyenMaiChiTietServlet extends BaseServlet {

    private final ChuongTrinhKhuyenMaiRepository ctkmRepo = new ChuongTrinhKhuyenMaiRepository();
    private final ChiTietChuongTrinhKhuyenMaiRepository chiTietRepo = new ChiTietChuongTrinhKhuyenMaiRepository();
    private final ChiTietSanPhamRepository bienTheRepo = new ChiTietSanPhamRepository();
    private final KhuyenMaiService service = new KhuyenMaiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = docLong(req, "id");
        ChuongTrinhKhuyenMai ct = ctkmRepo.timTheoId(id);
        if (ct == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/khuyen-mai", "Không tìm thấy chương trình khuyến mãi");
            return;
        }
        List<ChiTietChuongTrinhKhuyenMai> dsChiTiet = chiTietRepo.timTheoChuongTrinh(id);

        // gia sau giam cua tung dong (key = ma dong chi tiet) de JSP hien cot "Gia sau giam"
        Map<Long, BigDecimal> giaSauGiam = new HashMap<>();
        Set<Long> daCo = new HashSet<>();
        for (ChiTietChuongTrinhKhuyenMai d : dsChiTiet) {
            giaSauGiam.put(d.getMaChiTietChuongTrinhKhuyenMai(),
                    KhuyenMaiService.tinhGia(d.getChiTietSanPham().getGiaGoc(), d.getLoaiGiam(), d.getGiaTriGiam()));
            daCo.add(d.getChiTietSanPham().getMaChiTietSanPham());
        }

        // note code nhu nay nhe ban: dropdown chi hien bien the DANG_BAN va CHUA co trong chuong trinh nay (tranh trung UNIQUE)
        List<ChiTietSanPham> dsBienThe = new ArrayList<>();
        for (ChiTietSanPham b : bienTheRepo.layCoDieuKien("x.trangThai = 'DANG_BAN'", "x.maHang asc", 500)) {
            if (!daCo.contains(b.getMaChiTietSanPham())) dsBienThe.add(b);
        }

        // dang sua 1 dong: chi nhan neu dong do thuoc chuong trinh nay
        Long suaDong = docLong(req, "suaDong");
        if (suaDong != null) {
            ChiTietChuongTrinhKhuyenMai dong = chiTietRepo.timTheoId(suaDong);
            if (dong != null && id.equals(dong.getMaChuongTrinhKhuyenMai())) req.setAttribute("dongSua", dong);
        }

        req.setAttribute("ct", ct);
        req.setAttribute("dsChiTiet", dsChiTiet);
        req.setAttribute("giaSauGiam", giaSauGiam);
        req.setAttribute("dsBienThe", dsBienThe);
        hienThi(req, resp, "khuyen-mai/chi-tiet", "khuyen-mai", "Chi tiết khuyến mãi");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long maChuongTrinh = docLong(req, "id");
        String duongDan = "/wp-admin/khuyen-mai/chi-tiet?id=" + maChuongTrinh;
        String hanhDong = docChuoi(req, "hanhDong");

        try {
            if ("go".equals(hanhDong)) {
                service.goChiTiet(maChuongTrinh, docLong(req, "idDong"));
                chuyenHuong(req, resp, duongDan, "Đã gỡ sản phẩm khỏi chương trình");
                return;
            }

            // them / sua: doc 3 o nhap
            String loaiGiam = docChuoi(req, "loaiGiam");
            BigDecimal giaTriGiam = NhapLieuUtil.docSo(docChuoi(req, "giaTriGiam"), "Giá trị giảm");
            Integer soLuongGioiHan = NhapLieuUtil.docSoNguyen(docChuoi(req, "soLuongGioiHan"), "Giới hạn số lượng");   // trong = khong gioi han

            if ("sua".equals(hanhDong)) {
                service.suaChiTiet(maChuongTrinh, docLong(req, "idDong"), loaiGiam, giaTriGiam, soLuongGioiHan);
                chuyenHuong(req, resp, duongDan, "Đã cập nhật mức giảm");
            } else {
                service.themChiTiet(maChuongTrinh, docLong(req, "maChiTietSanPham"), loaiGiam, giaTriGiam, soLuongGioiHan);
                chuyenHuong(req, resp, duongDan, "Đã thêm sản phẩm vào chương trình");
            }
        } catch (NghiepVuException e) {
            chuyenHuongLoi(req, resp, duongDan, e.getMessage());
        }
    }
}
