package controller; // TODO: đổi nếu package servlet của bạn tên khác

import com.fasterxml.jackson.databind.ObjectMapper;
import service.BaoCaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/** Chỉ QUAN_LY / QUAN_TRI được truy cập: cấu hình trong PhanQuyenFilter cho /wp-admin/bao-cao/**. */
@WebServlet(urlPatterns = "/wp-admin/bao-cao/*")
public class BaoCaoServlet extends HttpServlet {

    private static final long TOI_DA_NGAY = 366;

    private final BaoCaoService service = new BaoCaoService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) trang(req, resp);
        else if (path.equals("/top-san-pham")) topSanPham(req, resp);
        else if (path.equals("/xuat-excel")) xuatCsv(req, resp);
        else resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // ---------------------------------------------------------- GET /wp-admin/bao-cao
    private void trang(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate[] ky = docKhoang(req);
        YearMonth thang = docThang(req.getParameter("thangTopSp"));

        var bieuDo = service.layDoanhThuBieuDo(ky[0], ky[1]);
        req.setAttribute("tuNgay", ky[0]);
        req.setAttribute("denNgay", ky[1]);
        req.setAttribute("thangTopSp", thang.toString());
        req.setAttribute("kpi", service.layKpi(ky[0], ky[1]));
        req.setAttribute("chartLabels", mapper.writeValueAsString(bieuDo.get("nhan")));
        req.setAttribute("chartValues", mapper.writeValueAsString(bieuDo.get("giaTri")));
        req.setAttribute("topSp", service.layTopSanPhamTheoThang(thang.getYear(), thang.getMonthValue()));
        req.setAttribute("topKh", service.layTopKhachHang(ky[0], ky[1]));
        req.setAttribute("topNv", service.layTopNhanVien(ky[0], ky[1]));
        req.setAttribute("coCau", service.layCoCauTheoLoai(ky[0], ky[1]));
        req.setAttribute("hoaDon", service.layHoaDonDoiSoat(ky[0], ky[1]));
        req.getRequestDispatcher("/trang-chu-backend/bao-cao.jsp").forward(req, resp); // TODO: chỉnh theo vị trí JSP thật
    }

    // ---------------------------------------------------------- GET /wp-admin/bao-cao/top-san-pham?thang=yyyy-MM
    private void topSanPham(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        YearMonth thang;
        try {
            thang = YearMonth.parse(req.getParameter("thang"));
        } catch (DateTimeParseException | NullPointerException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), Map.of("loi", "Tháng không hợp lệ, cần định dạng yyyy-MM"));
            return;
        }
        mapper.writeValue(resp.getWriter(), service.layTopSanPhamTheoThang(thang.getYear(), thang.getMonthValue()));
    }

    // ---------------------------------------------------------- GET /wp-admin/bao-cao/xuat-excel
    private void xuatCsv(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LocalDate[] ky = docKhoang(req);
        List<Map<String, Object>> ds = service.layHoaDonDoiSoat(ky[0], ky[1]);

        resp.setContentType("text/csv; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"hoa-don-" + ky[0] + "_" + ky[1] + ".csv\"");
        PrintWriter w = resp.getWriter();
        w.write('\uFEFF'); // BOM để Excel đọc đúng tiếng Việt
        w.write("Mã tra cứu,Ngày đặt,Khách hàng,Nhân viên xác nhận,Tổng tiền,Trạng thái\r\n");
        for (Map<String, Object> r : ds) {
            w.write(String.join(",",
                    csv(r.get("maTraCuu")), csv(r.get("ngayDatHang")), csv(r.get("khachHang")),
                    csv(r.get("nhanVien")), csv(r.get("tongTien")), csv(r.get("trangThai"))));
            w.write("\r\n");
        }
        w.flush();
    }

    /** Bọc ngoặc kép + chặn CSV injection (ô bắt đầu bằng = + - @). */
    private static String csv(Object v) {
        String s = v == null ? "" : v.toString();
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0) s = "'" + s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    // ---------------------------------------------------------- đọc & kiểm tra tham số
    private static LocalDate[] docKhoang(HttpServletRequest req) {
        LocalDate den = parseNgay(req.getParameter("denNgay"), LocalDate.now());
        LocalDate tu = parseNgay(req.getParameter("tuNgay"), den.minusDays(6));
        if (tu.isAfter(den)) { LocalDate t = tu; tu = den; den = t; }
        if (java.time.temporal.ChronoUnit.DAYS.between(tu, den) + 1 > TOI_DA_NGAY) tu = den.minusDays(TOI_DA_NGAY - 1);
        return new LocalDate[]{tu, den};
    }

    private static LocalDate parseNgay(String s, LocalDate macDinh) {
        try { return (s == null || s.isBlank()) ? macDinh : LocalDate.parse(s); }
        catch (DateTimeParseException e) { return macDinh; }
    }

    private static YearMonth docThang(String s) {
        try { return (s == null || s.isBlank()) ? YearMonth.now() : YearMonth.parse(s); }
        catch (DateTimeParseException e) { return YearMonth.now(); }
    }
}
