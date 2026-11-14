package controller;

import dto.BoLoc;
import dto.PhanTrang;
import entity.HoaDon;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.HoaDonRepository;

import java.io.IOException;

/**
 * Danh sach don hang: /wp-admin/don-hang  (module M7).
 * Bang lien quan: HoaDon. Loc theo trang thai, tim theo ma tra cuu / ten nguoi nhan / SDT.
 */
@WebServlet("/wp-admin/don-hang")
public class HoaDonServlet extends BaseServlet {

    private final HoaDonRepository hoaDonRepo = new HoaDonRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String trangThai = docChuoi(req, "trangThai");

        BoLoc loc = new BoLoc()
                .cotTim("maTraCuu,tenNguoiNhan,soDienThoaiNguoiNhan")
                .tuKhoa(docChuoi(req, "tuKhoa"))
                .cotTrangThai("trangThai")
                .trangThai(trangThai)
                .sapXep("x.maHoaDon desc")
                .trang(docSo(req, "trang", 1))
                .kichThuoc(10);

        // note code nhu nay nhe ban: GIO_HANG la gio hang cua khach, chua phai don -> chi hien khi admin chu dong chon loc "Gio hang"
        if (trangThai.isEmpty()) loc.dieuKienThem("x.trangThai <> 'GIO_HANG'");

        PhanTrang<HoaDon> pt = hoaDonRepo.timKiem(loc);
        req.setAttribute("pt", pt);
        req.setAttribute("duongDan", "/wp-admin/don-hang");
        req.setAttribute("soChoXacNhan", hoaDonRepo.demCoDieuKien("x.trangThai = 'CHO_XAC_NHAN'"));
        hienThi(req, resp, "don-hang/danh-sach", "don-hang", "Đơn hàng");
    }
}
