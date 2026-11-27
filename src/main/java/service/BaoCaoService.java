package service;

import util.HibernateConfig;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BaoCaoService {

    private static final BigDecimal TY_LE_GIA_VON = new BigDecimal("0.70");
    private static final BigDecimal TRAM = BigDecimal.valueOf(100);

    private static final String DK_DOANH_THU =
            " hd.trangThai = 'HOAN_THANH' AND hd.ngayGiaoThanhCong >= :tu AND hd.ngayGiaoThanhCong < :den ";

    private static BigDecimal bd(Object o) { return o == null ? BigDecimal.ZERO : new BigDecimal(o.toString()); }
    private static long lg(Object o) { return o == null ? 0 : ((Number) o).longValue(); }

    private static String ngayGio(Object o) {
        LocalDateTime t = null;
        if (o instanceof Timestamp) t = ((Timestamp) o).toLocalDateTime();
        else if (o instanceof LocalDateTime) t = (LocalDateTime) o;
        return t == null ? "" : t.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private static Map<String, Object> row(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }

    private static BigDecimal tyLe(BigDecimal phan, BigDecimal tong) {
        return tong.signum() == 0 ? BigDecimal.ZERO
                : phan.multiply(TRAM).divide(tong, 1, RoundingMode.HALF_UP);
    }

    private List<Object[]> query(String sql, Map<String, Object> params) {
        try (Session s = HibernateConfig.getFACTORY().openSession()) {
            var q = s.createNativeQuery(sql);
            params.forEach(q::setParameter);
            @SuppressWarnings("unchecked")
            List<Object[]> kq = q.getResultList();
            return kq;
        }
    }

    private static Map<String, Object> khoang(LocalDate tuNgay, LocalDate denNgay) {
        return Map.of("tu", tuNgay.atStartOfDay(), "den", denNgay.plusDays(1).atStartOfDay());
    }

    public Map<String, Object> layKpi(LocalDate tuNgay, LocalDate denNgay) {
        var p = khoang(tuNgay, denNgay);

        Object[] a = query("SELECT COUNT(*) AS soDon, COALESCE(SUM(hd.tongTien),0) AS doanhThu FROM dbo.HoaDon hd WHERE" + DK_DOANH_THU, p).get(0);
        long soDon = lg(a[0]);
        BigDecimal doanhThu = bd(a[1]);

        Object[] g = query("SELECT COALESCE(SUM(ct.thanhTien),0) AS tongTien, 0 AS dummy FROM dbo.HoaDonChiTiet ct "
                + "JOIN dbo.HoaDon hd ON hd.maHoaDon = ct.maHoaDon WHERE" + DK_DOANH_THU, p).get(0);
        BigDecimal giaVon = bd(g[0]).multiply(TY_LE_GIA_VON).setScale(0, RoundingMode.HALF_UP);
        BigDecimal loiNhuan = doanhThu.subtract(giaVon);

        Object[] h = query("SELECT COUNT(*) AS soDonHuy, COALESCE(SUM(hd.tongTien),0) AS giaTriHuy FROM dbo.HoaDon hd "
                + "WHERE hd.trangThai = 'DA_HUY' AND hd.ngayHuy >= :tu AND hd.ngayHuy < :den", p).get(0);

        return row(
                "doanhThu", doanhThu,
                "soDon", soDon,
                "aov", soDon == 0 ? BigDecimal.ZERO : doanhThu.divide(BigDecimal.valueOf(soDon), 0, RoundingMode.HALF_UP),
                "loiNhuan", loiNhuan,
                "bienLoiNhuan", tyLe(loiNhuan, doanhThu),
                "soDonHuy", lg(h[0]),
                "giaTriHuy", bd(h[1]));
    }

    public Map<String, List<?>> layDoanhThuBieuDo(LocalDate tuNgay, LocalDate denNgay) {
        boolean theoThang = ChronoUnit.DAYS.between(tuNgay, denNgay) + 1 > 60;
        String khoa = theoThang ? "CONVERT(VARCHAR(7), hd.ngayGiaoThanhCong, 120)"
                : "CONVERT(VARCHAR(10), hd.ngayGiaoThanhCong, 120)";
        List<Object[]> kq = query("SELECT " + khoa + " AS nhan, SUM(hd.tongTien) AS giaTri FROM dbo.HoaDon hd WHERE" + DK_DOANH_THU
                + " GROUP BY " + khoa + " ORDER BY 1", khoang(tuNgay, denNgay));

        Map<String, BigDecimal> dayDu = new LinkedHashMap<>();
        if (theoThang) {
            for (YearMonth m = YearMonth.from(tuNgay); !m.isAfter(YearMonth.from(denNgay)); m = m.plusMonths(1))
                dayDu.put(m.toString(), BigDecimal.ZERO);
        } else {
            for (LocalDate d = tuNgay; !d.isAfter(denNgay); d = d.plusDays(1)) dayDu.put(d.toString(), BigDecimal.ZERO);
        }
        for (Object[] r : kq) dayDu.put((String) r[0], bd(r[1]));
        return Map.of("nhan", new ArrayList<>(dayDu.keySet()), "giaTri", new ArrayList<>(dayDu.values()));
    }

    public List layTopSanPhamTheoThang(int nam, int thang) {
        YearMonth ym = YearMonth.of(nam, thang);
        Map<String, Object> p = Map.of("tu", ym.atDay(1).atStartOfDay(), "den", ym.plusMonths(1).atDay(1).atStartOfDay());

        BigDecimal tongThang = bd(query("SELECT COALESCE(SUM(ct.thanhTien),0) AS tongThang, 0 AS dummy FROM dbo.HoaDonChiTiet ct "
                + "JOIN dbo.HoaDon hd ON hd.maHoaDon = ct.maHoaDon WHERE" + DK_DOANH_THU, p).get(0)[0]);

        List<Object[]> kq = query("SELECT TOP 10 ct.maHang, MAX(ct.tenSanPham) AS tenSanPham, SUM(ct.soLuong) AS soLuong, SUM(ct.thanhTien) AS doanhThu "
                + "FROM dbo.HoaDonChiTiet ct JOIN dbo.HoaDon hd ON hd.maHoaDon = ct.maHoaDon WHERE" + DK_DOANH_THU
                + "GROUP BY ct.maHang ORDER BY SUM(ct.soLuong) DESC, SUM(ct.thanhTien) DESC", p);

        List ds = new ArrayList<>();
        int hang = 1;
        for (Object[] r : kq) {
            BigDecimal dt = bd(r[3]);
            ds.add(row("hang", hang++, "maHang", r[0], "tenSanPham", r[1],
                    "soLuong", lg(r[2]), "doanhThu", dt, "tyTrong", tyLe(dt, tongThang)));
        }
        return ds;
    }

    public List layTopKhachHang(LocalDate tuNgay, LocalDate denNgay) {
        List ds = new ArrayList<>();
        int hang = 1;
        for (Object[] r : query("SELECT TOP 10 kh.hoTen, kh.soDienThoai, COUNT(*) AS soDon, SUM(hd.tongTien) AS tongChiTieu "
                + "FROM dbo.HoaDon hd JOIN dbo.KhachHang kh ON kh.maKhachHang = hd.maKhachHang WHERE" + DK_DOANH_THU
                + "GROUP BY kh.maKhachHang, kh.hoTen, kh.soDienThoai ORDER BY SUM(hd.tongTien) DESC", khoang(tuNgay, denNgay)))
            ds.add(row("hang", hang++, "hoTen", r[0], "soDienThoai", r[1], "soDon", lg(r[2]), "tongChiTieu", bd(r[3])));
        return ds;
    }

    public List layTopNhanVien(LocalDate tuNgay, LocalDate denNgay) {
        List ds = new ArrayList<>();
        int hang = 1;
        for (Object[] r : query("SELECT TOP 10 nv.hoTen, COUNT(*) AS soDonXuLy, SUM(hd.tongTien) AS doanhSo "
                + "FROM dbo.HoaDon hd JOIN dbo.NhanVien nv ON nv.maNhanVien = hd.maNhanVienXacNhan WHERE" + DK_DOANH_THU
                + "GROUP BY nv.maNhanVien, nv.hoTen ORDER BY SUM(hd.tongTien) DESC", khoang(tuNgay, denNgay)))
            ds.add(row("hang", hang++, "hoTen", r[0], "soDonXuLy", lg(r[1]), "doanhSo", bd(r[2])));
        return ds;
    }

    public List layCoCauTheoLoai(LocalDate tuNgay, LocalDate denNgay) {
        List<Object[]> kq = query("SELECT sp.loaiSanPham, SUM(ct.thanhTien) AS doanhThu FROM dbo.HoaDonChiTiet ct "
                + "JOIN dbo.HoaDon hd ON hd.maHoaDon = ct.maHoaDon "
                + "JOIN dbo.ChiTietSanPham c ON c.maChiTietSanPham = ct.maChiTietSanPham "
                + "JOIN dbo.SanPham sp ON sp.maSanPham = c.maSanPham WHERE" + DK_DOANH_THU
                + "GROUP BY sp.loaiSanPham ORDER BY SUM(ct.thanhTien) DESC", khoang(tuNgay, denNgay));
        BigDecimal tong = kq.stream().map(r -> bd(r[1])).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, String> ten = Map.of("VOT", "Vợt", "BONG", "Bóng", "PHU_KIEN", "Phụ kiện", "TUI", "Túi");
        List ds = new ArrayList<>();
        for (Object[] r : kq)
            ds.add(row("loai", ten.getOrDefault((String) r[0], (String) r[0]), "doanhThu", bd(r[1]), "tyTrong", tyLe(bd(r[1]), tong)));
        return ds;
    }

    public List layHoaDonDoiSoat(LocalDate tuNgay, LocalDate denNgay) {
        List ds = new ArrayList<>();
        for (Object[] r : query("SELECT TOP 1000 hd.maHoaDon, hd.maTraCuu, hd.ngayDatHang, kh.hoTen AS khachHang, nv.hoTen AS nhanVien, hd.tongTien, hd.trangThai "
                + "FROM dbo.HoaDon hd JOIN dbo.KhachHang kh ON kh.maKhachHang = hd.maKhachHang "
                + "LEFT JOIN dbo.NhanVien nv ON nv.maNhanVien = hd.maNhanVienXacNhan "
                + "WHERE hd.trangThai <> 'GIO_HANG' AND hd.ngayDatHang >= :tu AND hd.ngayDatHang < :den "
                + "ORDER BY hd.ngayDatHang DESC", khoang(tuNgay, denNgay)))
            ds.add(row("maHoaDon", r[0], "maTraCuu", r[1], "ngayDatHang", ngayGio(r[2]), "khachHang", r[3],
                    "nhanVien", r[4], "tongTien", bd(r[5]), "trangThai", r[6]));
        return ds;
    }
}