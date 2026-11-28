package service;

import dto.CotBieuDoDTO;
import dto.CoCauDanhMucDTO;
import dto.OThongKeDTO;
import dto.TangTruongDTO;
import dto.TongQuanDTO;
import repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        // ---- cac moc thoi gian ----
        LocalDateTime dauThang = homNay.withDayOfMonth(1).atStartOfDay();
        LocalDateTime cuoiThang = dauThang.plusMonths(1);
        LocalDateTime denHetHomNay = homNay.plusDays(1).atStartOfDay();
        // note code nhu nay nhe ban: so sanh CUNG KY - thang nay moi chay N ngay thi thang truoc cung chi lay N ngay dau
        // (neu so sanh ca thang truoc thi dau thang luon bi am). Math.min de thang truoc ngan hon (vd thang 2) khong bi tran sang thang sau
        LocalDateTime dauThangTruoc = dauThang.minusMonths(1);
        int soNgayThangTruoc = YearMonth.from(dauThangTruoc.toLocalDate()).lengthOfMonth();
        LocalDateTime denCungKyTruoc = dauThangTruoc.plusDays(Math.min(homNay.getDayOfMonth(), soNgayThangTruoc));

        // ---- cac the so ----
        tq.doanhThuThang = thongKeRepo.doanhThu(dauThang, cuoiThang);
        tq.tangTruongDoanhThu = tinhTangTruong(tq.doanhThuThang, thongKeRepo.doanhThu(dauThangTruoc, denCungKyTruoc));

        tq.donMoi = hoaDonRepo.demCoDieuKien("x.trangThai = 'CHO_XAC_NHAN'");

        // note code nhu nay nhe ban: so khach = khach that (bo tai khoan "Khach le"); tang truong = so khach MOI cung ky
        tq.soKhachHang = thongKeRepo.demKhachThat();
        tq.khachMoiThang = thongKeRepo.demKhachMoi(dauThang, denHetHomNay);
        long khachMoiKyTruoc = thongKeRepo.demKhachMoi(dauThangTruoc, denCungKyTruoc);
        tq.tangTruongKhachHang = tinhTangTruong(BigDecimal.valueOf(tq.khachMoiThang), BigDecimal.valueOf(khachMoiKyTruoc));

        // note code nhu nay nhe ban: gia tri trung binh moi don = doanh thu thang / so don HOAN_THANH (chia 0 thi tra 0)
        tq.soDonHoanThanhThang = thongKeRepo.demDonHoanThanh(dauThang, cuoiThang);
        tq.giaTriTrungBinhDon = tq.soDonHoanThanhThang == 0 ? BigDecimal.ZERO
                : tq.doanhThuThang.divide(BigDecimal.valueOf(tq.soDonHoanThanhThang), 0, RoundingMode.HALF_UP);

        // ---- khoi "Can xu ly" ----
        tq.donChoXacNhan = tq.donMoi;
        tq.donDangGiao = hoaDonRepo.demCoDieuKien("x.trangThai = 'DANG_GIAO'");
        tq.tonKhoCanhBao = chiTietRepo.demCoDieuKien(TON_THAP);
        tq.khuyenMaiSapChay = khuyenMaiRepo.demCoDieuKien("x.trangThai = 'SAP_CHAY'");

        // ---- bieu do ----
        tq.kieuBieuDo = "thang".equals(kieuBieuDo) ? "thang" : "ngay";
        tq.bieuDo = "thang".equals(tq.kieuBieuDo) ? bieuDoThang(homNay) : bieuDoNgay(homNay);
        tq.coCauDanhMuc = coCauDanhMuc(dauThang, cuoiThang);

        // ---- cac bang ----
        // note code nhu nay nhe ban: don gan day khong lay GIO_HANG (gio hang chua phai don hang)
        tq.donGanDay = hoaDonRepo.layCoDieuKien("x.trangThai <> 'GIO_HANG'", "x.ngayDatHang desc", 5);
        tq.tonKhoThap = chiTietRepo.layCoDieuKien(TON_THAP, "x.soLuongTon asc", 5);
        tq.topBanChay = thongKeRepo.topBanChay(5);
        tq.thongKeBang = demCacBang();     // 18 bang, bam vao o nao thi mo trang quan ly cua bang do
        return tq;
    }

    // note code nhu nay nhe ban: tang truong % = (nay - truoc) / truoc * 100, lam tron 1 so le. Ky truoc = 0 thi khong chia duoc
    private TangTruongDTO tinhTangTruong(BigDecimal nay, BigDecimal truoc) {
        if (truoc.signum() == 0) return new TangTruongDTO("", "flat", false);
        BigDecimal phanTram = nay.subtract(truoc).multiply(BigDecimal.valueOf(100))
                .divide(truoc, 1, RoundingMode.HALF_UP);
        if (phanTram.signum() == 0) return new TangTruongDTO("0%", "flat", true);
        String so = phanTram.stripTrailingZeros().toPlainString();      // 12.5 -> "12.5", 5.0 -> "5"
        return phanTram.signum() > 0
                ? new TangTruongDTO("+" + so + "%", "up", true)
                : new TangTruongDTO(so + "%", "down", true);            // so da co dau "-"
    }

    // note code nhu nay nhe ban: ty trong moi danh muc = tien danh muc / tong tien * 100
    private List<CoCauDanhMucDTO> coCauDanhMuc(LocalDateTime tu, LocalDateTime den) {
        List<Object[]> dong = thongKeRepo.doanhThuTheoDanhMuc(tu, den);
        BigDecimal tong = BigDecimal.ZERO;
        for (Object[] d : dong) tong = tong.add(new BigDecimal(d[1].toString()));

        List<CoCauDanhMucDTO> ds = new ArrayList<CoCauDanhMucDTO>();
        for (Object[] d : dong) {
            BigDecimal tien = new BigDecimal(d[1].toString());
            int phanTram = tong.signum() == 0 ? 0
                    : tien.multiply(BigDecimal.valueOf(100)).divide(tong, 0, RoundingMode.HALF_UP).intValue();
            ds.add(new CoCauDanhMucDTO(d[0].toString(), tien, phanTram));
        }
        return ds;
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

    // note code nhu nay nhe ban: dem dong cua du 18 bang de hien o muc "Tong quan du lieu" (bam vao o nao thi ra trang quan ly cua bang do)
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
        // note code nhu nay nhe ban: Lich su trang thai co trang rieng /wp-admin/lich-su-trang-thai (truoc do tro nham ve don-hang)
        ds.add(new OThongKeDTO("Lịch sử trạng thái", new LichSuTrangThaiHoaDonRepository().dem(), a + "lich-su-trang-thai"));
        return ds;
    }
}