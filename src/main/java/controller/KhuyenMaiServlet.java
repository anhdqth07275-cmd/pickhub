package controller;

import dto.BoLoc;
import dto.DongKhuyenMaiDTO;
import dto.PhanTrang;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Chuong trinh khuyen mai: /wp-admin/khuyen-mai  (module M4). Chi QUAN_LY, QUAN_TRI.
 * Bang lien quan: ChuongTrinhKhuyenMai, ChiTietChuongTrinhKhuyenMai.
 *
 * GIAO DIEN LAM LAI THEO ANH MAU: danh sach + POPUP them / sua (ten, thoi gian, tim kiem + TICH CHON san pham + muc giam).
 *   GET                          -> danh sach (tim kiem + loc trang thai + phan trang)
 *   GET  ?hanhDong=form[&id=]    -> danh sach + MO POPUP them / sua chuong trinh
 *   POST hanhDong=luu            -> luu chuong trinh KEM san pham duoc tich chon (KhuyenMaiService.luuChuongTrinhKemSanPham)
 *   POST hanhDong=doi-trang-thai -> tam dung / chay lai
 *   POST hanhDong=xoa            -> xoa chuong trinh (chi khi chua co hoa don nao dung)
 * Trang thai SAP_CHAY -> DANG_CHAY -> KET_THUC do KhuyenMaiScheduler tu doi theo thoi gian.
 */
@WebServlet("/wp-admin/khuyen-mai")
public class KhuyenMaiServlet extends BaseServlet {

    private static final int SO_GOI_Y = 30;      // so san pham hien san trong popup, muon tim them thi go o o "Tim ma hoac ten san pham"

    private final ChuongTrinhKhuyenMaiRepository repo = new ChuongTrinhKhuyenMaiRepository();
    private final ChiTietChuongTrinhKhuyenMaiRepository chiTietRepo = new ChiTietChuongTrinhKhuyenMaiRepository();
    private final ChiTietSanPhamRepository bienTheRepo = new ChiTietSanPhamRepository();
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
            // sua: cac san pham da co trong chuong trinh hien san o dang "da tich chon"; them moi: chua chon san pham nao
            List<DongKhuyenMaiDTO> dsChon = id == null ? new ArrayList<DongKhuyenMaiDTO>() : service.layDongDaChon(id);
            moPopup(req, resp, ct, dsChon);
            return;
        }
        napDanhSach(req);
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

        // ---- xoa ----
        if ("xoa".equals(hanhDong)) {
            try {
                service.xoaChuongTrinh(docLong(req, "id"));
                chuyenHuong(req, resp, "/wp-admin/khuyen-mai", "Đã xóa chương trình khuyến mãi");
            } catch (NghiepVuException e) {
                chuyenHuongLoi(req, resp, "/wp-admin/khuyen-mai", e.getMessage());
            }
            return;
        }

        // ---- luu (them / sua) chuong trinh KEM san pham tich chon ----
        Long id = docLong(req, "id");
        ChuongTrinhKhuyenMai ct = id == null ? new ChuongTrinhKhuyenMai() : repo.timTheoId(id);
        if (ct == null) {
            chuyenHuongLoi(req, resp, "/wp-admin/khuyen-mai", "Không tìm thấy chương trình khuyến mãi");
            return;
        }
        List<DongKhuyenMaiDTO> dsChon = docDongDaChon(req, id);
        try {
            ct.setTenChuongTrinh(docChuoi(req, "tenChuongTrinh"));
            ct.setMoTa(docChuoi(req, "moTa").isEmpty() ? null : docChuoi(req, "moTa"));
            ct.setNgayBatDau(NhapLieuUtil.docThoiGian(docChuoi(req, "ngayBatDau"), "Ngày bắt đầu"));
            ct.setNgayKetThuc(NhapLieuUtil.docThoiGian(docChuoi(req, "ngayKetThuc"), "Ngày kết thúc"));

            // note code nhu nay nhe ban: suaSanPham=1 chi co khi popup HIEN bang san pham. Chuong trinh da ket thuc thi popup an bang
            // nay -> khong co co, service se KHONG dung toi danh sach san pham (tranh hieu nham "khong tich gi" la "xoa het san pham")
            boolean suaSanPham = "1".equals(docChuoi(req, "suaSanPham"));
            // maNguoiTao = nhan vien dang dang nhap (lay tu session), chi luu khi them moi
            service.luuChuongTrinhKemSanPham(ct, taiKhoan(req).getId(), dsChon, suaSanPham);
            chuyenHuong(req, resp, "/wp-admin/khuyen-mai", "Đã lưu chương trình khuyến mãi");
        } catch (NghiepVuException e) {
            // note code nhu nay nhe ban: loi validate thi MO LAI POPUP kem du lieu vua nhap (khong redirect) de nguoi dung sua tiep, khong phai tich lai
            req.setAttribute("loi", e.getMessage());
            moPopup(req, resp, ct, dsChon);
        }
    }

    // ---------------- ham ho tro ----------------

    /** Nap du lieu cho phan DANH SACH (tim kiem + loc trang thai + phan trang + so san pham moi chuong trinh). */
    private void napDanhSach(HttpServletRequest req) {
        BoLoc loc = new BoLoc()
                .cotTim("tenChuongTrinh")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(docChuoi(req, "trangThai"))
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);
        PhanTrang<ChuongTrinhKhuyenMai> pt = repo.timKiem(loc);
        req.setAttribute("pt", pt);
        req.setAttribute("duongDan", "/wp-admin/khuyen-mai");

        // so san pham cua tung chuong trinh (cot "So SP"): 1 cau truy van cho ca trang
        List<Long> dsMa = new ArrayList<>();
        for (ChuongTrinhKhuyenMai c : pt.getDanhSach()) dsMa.add(c.getMaChuongTrinhKhuyenMai());
        req.setAttribute("soSanPham", chiTietRepo.demTheoCacChuongTrinh(dsMa));
    }

    /**
     * Hien danh sach va MO POPUP them / sua.
     * dsChon = cac dong da tich chon; phia sau do them cac san pham goi y (chua chon) de bang khong bi trong.
     */
    private void moPopup(HttpServletRequest req, HttpServletResponse resp, ChuongTrinhKhuyenMai ct, List<DongKhuyenMaiDTO> dsChon)
            throws ServletException, IOException {
        napDanhSach(req);

        // note code nhu nay nhe ban: chuong trinh da KET_THUC (theo DB) thi khong cho sua san pham -> popup an bang san pham
        boolean khoaSanPham = ct.getMaChuongTrinhKhuyenMai() != null && "KET_THUC".equals(ct.getTrangThai());
        List<DongKhuyenMaiDTO> dsDong = new ArrayList<>(dsChon);
        if (!khoaSanPham) {
            Set<Long> daCo = new HashSet<>();
            for (DongKhuyenMaiDTO d : dsChon) daCo.add(d.getBienThe().getMaChiTietSanPham());
            dsDong.addAll(service.timSanPhamDeChon("", SO_GOI_Y, daCo));
        }
        req.setAttribute("ct", ct);
        req.setAttribute("dsDong", dsDong);
        req.setAttribute("khoaSanPham", khoaSanPham);
        req.setAttribute("moPopup", Boolean.TRUE);
        hienThi(req, resp, "khuyen-mai/danh-sach", "khuyen-mai", "Chương trình khuyến mãi");
    }

    /** Doc cac dong DA TICH CHON tu form: ten o nhap la loaiGiam_<ma>, giaTri_<ma>, soLuong_<ma> voi <ma> la ma bien the. */
    private List<DongKhuyenMaiDTO> docDongDaChon(HttpServletRequest req, Long maChuongTrinh) {
        List<DongKhuyenMaiDTO> ds = new ArrayList<>();
        String[] cacMa = req.getParameterValues("chon");
        if (cacMa == null) return ds;

        // dong da co trong chuong trinh (de hien lai "da ban" khi mo lai popup sau loi)
        Map<Long, DongKhuyenMaiDTO> dongCu = new HashMap<>();
        if (maChuongTrinh != null) {
            for (DongKhuyenMaiDTO d : service.layDongDaChon(maChuongTrinh)) dongCu.put(d.getBienThe().getMaChiTietSanPham(), d);
        }

        for (String ma : cacMa) {
            Long maBienThe;
            try {
                maBienThe = Long.valueOf(ma.trim());
            } catch (NumberFormatException e) {
                continue;
            }
            ChiTietSanPham bt = bienTheRepo.timTheoId(maBienThe);
            if (bt == null) continue;
            DongKhuyenMaiDTO d = new DongKhuyenMaiDTO(bt);
            d.setChon(true);
            d.setLoaiGiam(docChuoi(req, "loaiGiam_" + ma));
            d.setGiaTriGiam(docChuoi(req, "giaTri_" + ma));
            d.setSoLuongGioiHan(docChuoi(req, "soLuong_" + ma));
            DongKhuyenMaiDTO cu = dongCu.get(maBienThe);
            if (cu != null) {
                d.setMaDong(cu.getMaDong());
                d.setSoLuongDaBan(cu.getSoLuongDaBan());
            }
            ds.add(d);
        }
        return ds;
    }
}
