package service;

import dto.CotBieuDoDTO;
import dto.OThongKeDTO;
import dto.TongQuanDTO;
import repository.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gom so lieu cho trang Dashboard (module M8).
 */
public class ThongKeService {

    private final ThongKeRepository thongKeRepo = new ThongKeRepository();
    private final HoaDonRepository hoaDonRepo = new HoaDonRepository();
    private final ChiTietSanPhamRepository chiTietRepo = new ChiTietSanPhamRepository();
    private final KhachHangRepository khachHangRepo = new KhachHangRepository();
    private final ChuongTrinhKhuyenMaiRepository khuyenMaiRepo = new ChuongTrinhKhuyenMaiRepository();

    private static final String TON_THAP = "x.soLuongTon <= x.nguongCanhBaoTonKho";

    /** kieuBieuDo: "ngay" (7 ngay gan nhat) hoac "thang" (6 thang gan nhat) */
    public TongQuanDTO layTongQuan(String kieuBieuDo) {
        TongQuanDTO tq = new TongQuanDTO();
        LocalDate homNay = LocalDate.now();

        // ---- 4 the so ----
        LocalDateTime dauThang = homNay.withDayOfMonth(1).atStartOfDay();
        tq.doanhThuThang = thongKeRepo.doanhThu(dauThang, dauThang.plusMonths(1));
        tq.donMoi = hoaDonRepo.demCoDieuKien("x.trangThai = 'CHO_XAC_NHAN'");
        tq.soKhachHang = khachHangRepo.dem();

        // ---- khoi "Can xu ly" ----
        tq.donChoXacNhan = tq.donMoi;
        tq.donDangGiao = hoaDonRepo.demCoDieuKien("x.trangThai = 'DANG_GIAO'");
        tq.tonKhoCanhBao = chiTietRepo.demCoDieuKien(TON_THAP);
        tq.khuyenMaiSapChay = khuyenMaiRepo.demCoDieuKien("x.trangThai = 'SAP_CHAY'");

        // ---- bieu do ----
        tq.kieuBieuDo = "thang".equals(kieuBieuDo) ? "thang" : "ngay";
        tq.bieuDo = "thang".equals(tq.kieuBieuDo) ? bieuDoThang(homNay) : bieuDoNgay(homNay);

        // ---- cac bang ----
        // note code nhu nay nhe ban: don gan day khong lay GIO_HANG (gio hang chua phai don hang)
        tq.donGanDay = hoaDonRepo.layCoDieuKien("x.trangThai <> 'GIO_HANG'", "x.ngayDatHang desc", 5);
        tq.tonKhoThap = chiTietRepo.layCoDieuKien(TON_THAP, "x.soLuongTon asc", 5);
        tq.topBanChay = thongKeRepo.topBanChay(5);
        tq.thongKeBang = demCacBang();
        return tq;
    }

    // 7 cot: 6 ngay truoc + hom nay
    private List<CotBieuDoDTO> bieuDoNgay(LocalDate homNay) {
        LocalDate tu = homNay.minusDays(6);
        Map<String, BigDecimal> theoNgay = new HashMap<String, BigDecimal>();
        for (Object[] dong : thongKeRepo.doanhThuTheoNgay(tu.atStartOfDay())) {
            theoNgay.put(dong[0].toString().trim(), new BigDecimal(dong[1].toString()));
        }
        List<String> nhan = new ArrayList<String>();
        List<BigDecimal> giaTri = new ArrayList<BigDecimal>();
        for (int i = 0; i < 7; i++) {
            LocalDate ngay = tu.plusDays(i);
            nhan.add(nhanThu(ngay.getDayOfWeek()));
            BigDecimal v = theoNgay.get(ngay.toString());      // LocalDate.toString() = yyyy-MM-dd
            giaTri.add(v == null ? BigDecimal.ZERO : v);
        }
        return taoCot(nhan, giaTri);
    }

    // 6 cot: 5 thang truoc + thang nay
    private List<CotBieuDoDTO> bieuDoThang(LocalDate homNay) {
        YearMonth tu = YearMonth.from(homNay).minusMonths(5);
        Map<String, BigDecimal> theoThang = new HashMap<String, BigDecimal>();
        for (Object[] dong : thongKeRepo.doanhThuTheoThang(tu.atDay(1).atStartOfDay())) {
            theoThang.put(dong[0].toString().trim(), new BigDecimal(dong[1].toString()));
        }
        List<String> nhan = new ArrayList<String>();
        List<BigDecimal> giaTri = new ArrayList<BigDecimal>();
        for (int i = 0; i < 6; i++) {
            YearMonth thang = tu.plusMonths(i);
            nhan.add("T" + thang.getMonthValue());
            BigDecimal v = theoThang.get(thang.toString());    // YearMonth.toString() = yyyy-MM
            giaTri.add(v == null ? BigDecimal.ZERO : v);
        }
        return taoCot(nhan, giaTri);
    }

    // note code nhu nay nhe ban: chieu cao cot = gia tri / gia tri lon nhat * 100 (%). Cot cuoi cung la "hom nay" -> to mau xanh la
    private List<CotBieuDoDTO> taoCot(List<String> nhan, List<BigDecimal> giaTri) {
        BigDecimal max = BigDecimal.ZERO;
        for (BigDecimal v : giaTri) if (v.compareTo(max) > 0) max = v;

        List<CotBieuDoDTO> ds = new ArrayList<CotBieuDoDTO>();
        for (int i = 0; i < nhan.size(); i++) {
            int phanTram = max.signum() == 0 ? 0 : giaTri.get(i).multiply(BigDecimal.valueOf(100)).divide(max, 0, BigDecimal.ROUND_HALF_UP).intValue();
            if (giaTri.get(i).signum() > 0 && phanTram < 3) phanTram = 3;   // co du lieu thi cot van nhin thay
            ds.add(new CotBieuDoDTO(nhan.get(i), giaTri.get(i), phanTram, i == nhan.size() - 1));
        }
        return ds;
    }

    private String nhanThu(DayOfWeek thu) {
        return thu == DayOfWeek.SUNDAY ? "CN" : "T" + (thu.getValue() + 1);   // MONDAY=1 -> T2
    }

    // note code nhu nay nhe ban: dem dong cua du 18 bang de hien o muc "Tong quan du lieu" (bam vao ra module tuong ung)
    private List<OThongKeDTO> demCacBang() {
        String a = "/wp-admin/";
        List<OThongKeDTO> ds = new ArrayList<OThongKeDTO>();
        ds.add(new OThongKeDTO("Thương hiệu", new ThuongHieuRepository().dem(), a + "thuong-hieu"));
        ds.add(new OThongKeDTO("Danh mục", new DanhMucRepository().dem(), a + "danh-muc"));
        ds.add(new OThongKeDTO("Màu sắc", new MauSacRepository().dem(), a + "thuoc-tinh?loai=mausac"));
        ds.add(new OThongKeDTO("Khối lượng", new KhoiLuongRepository().dem(), a + "thuoc-tinh?loai=khoi-luong"));
        ds.add(new OThongKeDTO("Chất liệu", new ChatLieuRepository().dem(), a + "thuoc-tinh?loai=chat-lieu"));
        ds.add(new OThongKeDTO("Chu vi tay cầm", new ChuViTayCamRepository().dem(), a + "thuoc-tinh?loai=chu-vi"));
        ds.add(new OThongKeDTO("Sản phẩm", new SanPhamRepository().dem(), a + "san-pham"));
        ds.add(new OThongKeDTO("Chi tiết sản phẩm", chiTietRepo.dem(), a + "ton-kho"));
        ds.add(new OThongKeDTO("Hình ảnh sản phẩm", new HinhAnhSanPhamRepository().dem(), a + "san-pham"));
        ds.add(new OThongKeDTO("Nhân viên", new NhanVienRepository().dem(), a + "nhan-vien"));
        ds.add(new OThongKeDTO("Khách hàng", khachHangRepo.dem(), a + "khach-hang"));
        ds.add(new OThongKeDTO("Chương trình khuyến mãi", khuyenMaiRepo.dem(), a + "khuyen-mai"));
        ds.add(new OThongKeDTO("Chi tiết khuyến mãi", new ChiTietChuongTrinhKhuyenMaiRepository().dem(), a + "khuyen-mai"));
        ds.add(new OThongKeDTO("Phiếu giảm giá", new PhieuGiamGiaRepository().dem(), a + "phieu-giam-gia"));
        ds.add(new OThongKeDTO("Lượt dùng phiếu", new SuDungPhieuGiamGiaRepository().dem(), a + "phieu-giam-gia/su-dung"));
        ds.add(new OThongKeDTO("Hóa đơn", hoaDonRepo.dem(), a + "don-hang"));
        ds.add(new OThongKeDTO("Hóa đơn chi tiết", new HoaDonChiTietRepository().dem(), a + "don-hang"));
        ds.add(new OThongKeDTO("Lịch sử trạng thái", new LichSuTrangThaiHoaDonRepository().dem(), a + "don-hang"));
        return ds;
    }
}
