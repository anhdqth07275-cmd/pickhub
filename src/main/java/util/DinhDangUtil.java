package util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Ham dinh dang dung trong JSP (khai bao o WEB-INF/pickhub.tld, goi bang ${ph:tien(x)}).
 * Cac ham deu static va nhan Object de JSP truyen gia tri null cung khong loi.
 */
public class DinhDangUtil {

    private static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Map<String, String> NHAN = new HashMap<String, String>();

    static {
        // note code nhu nay nhe ban: ma trang thai trong DB -> chu hien thi tieng Viet
        NHAN.put("GIO_HANG", "Giỏ hàng");
        NHAN.put("CHO_XAC_NHAN", "Chờ xác nhận");
        NHAN.put("DA_XAC_NHAN", "Đã xác nhận");
        NHAN.put("DANG_GIAO", "Đang giao");
        NHAN.put("HOAN_THANH", "Hoàn thành");
        NHAN.put("DA_HUY", "Đã hủy");
        NHAN.put("HOAT_DONG", "Hoạt động");
        NHAN.put("NGUNG", "Ngừng");
        NHAN.put("KHOA", "Bị khóa");
        NHAN.put("CHUA_XAC_THUC", "Chưa xác thực");
        NHAN.put("DANG_BAN", "Đang bán");
        NHAN.put("NGUNG_BAN", "Ngừng bán");
        NHAN.put("HET_HANG", "Hết hàng");
        NHAN.put("SAP_CHAY", "Sắp chạy");
        NHAN.put("DANG_CHAY", "Đang chạy");
        NHAN.put("TAM_DUNG", "Tạm dừng");
        NHAN.put("KET_THUC", "Kết thúc");
        NHAN.put("HET_HAN", "Hết hạn");
        NHAN.put("DA_SU_DUNG", "Đã sử dụng");
        NHAN.put("DA_HOAN", "Đã hoàn");
        NHAN.put("QUAN_TRI", "Quản trị");
        NHAN.put("QUAN_LY", "Quản lý");
        NHAN.put("NHAN_VIEN", "Nhân viên");
        NHAN.put("VOT", "Vợt");
        NHAN.put("BONG", "Bóng");
        NHAN.put("PHU_KIEN", "Phụ kiện");
        NHAN.put("TUI", "Túi");
        NHAN.put("PHAN_TRAM", "Phần trăm");
        NHAN.put("SO_TIEN", "Số tiền");
    }

    /** 13459000 -> "13.459.000đ" */
    public static String tien(Object so) {
        if (so == null) return "0đ";
        DecimalFormatSymbols kyHieu = new DecimalFormatSymbols();
        kyHieu.setGroupingSeparator('.');
        return new DecimalFormat("#,##0", kyHieu).format(new BigDecimal(so.toString())) + "đ";
    }

    /** 12500000 -> "12,5" (don vi: trieu dong, dung cho nhan tren bieu do) */
    public static String trieu(Object so) {
        if (so == null) return "0";
        BigDecimal tr = new BigDecimal(so.toString()).divide(new BigDecimal("1000000"), 1, BigDecimal.ROUND_HALF_UP);
        return tr.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    public static String ngayGio(Object thoiDiem) {
        return thoiDiem instanceof LocalDateTime ? ((LocalDateTime) thoiDiem).format(NGAY_GIO) : "";
    }

    public static String ngay(Object thoiDiem) {
        if (thoiDiem instanceof LocalDateTime) return ((LocalDateTime) thoiDiem).format(NGAY);
        if (thoiDiem instanceof LocalDate) return ((LocalDate) thoiDiem).format(NGAY);
        return "";
    }

    public static String nhan(String maTrangThai) {
        if (maTrangThai == null) return "";
        String nhan = NHAN.get(maTrangThai);
        return nhan != null ? nhan : maTrangThai;
    }

    /** loaiGiam + giaTriGiam -> "15%" hoac "300.000đ" */
    public static String giamGia(String loaiGiam, Object giaTri) {
        if (giaTri == null) return "";
        if ("PHAN_TRAM".equals(loaiGiam)) {
            return new BigDecimal(giaTri.toString()).stripTrailingZeros().toPlainString() + "%";
        }
        return tien(giaTri);
    }
}
