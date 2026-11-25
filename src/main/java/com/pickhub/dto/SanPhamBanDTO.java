package com.pickhub.dto;

import com.pickhub.util.JsonUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 1 SAN PHAM tren luoi cua man hinh ban hang (the san pham): anh, ten, khoang gia + TOAN BO bien the cua no de popup "chon bien the".
 * note code nhu nay nhe ban: truoc day luoi hien tung BIEN THE (moi mau / khoi luong la 1 the). Gio gom lai thanh 1 the cho moi san pham;
 * bam vao the thi mo popup liet ke cac bien the (BienTheBanDTO) de nhan vien chon.
 */
public class SanPhamBanDTO {

    private final Long maSanPham;
    private final String tenSanPham;
    private final String thuongHieu;
    private final String anh;
    private final List<BienTheBanDTO> bienThe;

    // cac so dung de ve the, tinh san 1 lan trong constructor
    private final BigDecimal giaThapNhat;          // gia ban (sau khuyen mai) thap nhat trong cac bien the con hang
    private final BigDecimal giaCaoNhat;
    private final BigDecimal giaGocCuaGiaThap;     // gia goc cua bien the re nhat (de gach ngang khi co khuyen mai)
    private final int tongTon;
    private final boolean coKhuyenMai;

    public SanPhamBanDTO(List<BienTheBanDTO> bienThe) {
        BienTheBanDTO dau = bienThe.get(0);
        this.maSanPham = dau.getMaSanPham();
        this.tenSanPham = dau.getTenSanPham();
        this.thuongHieu = dau.getThuongHieu();
        this.bienThe = bienThe;

        // anh the = anh dau tien tim duoc cua cac bien the (bien the nao khong co anh rieng thi dung anh chinh cua san pham)
        String anhThe = null;
        for (BienTheBanDTO b : bienThe) {
            if (b.getAnh() != null) {
                anhThe = b.getAnh();
                break;
            }
        }
        this.anh = anhThe;

        // note code nhu nay nhe ban: khoang gia chi tinh tren bien the CON BAN DUOC (dang ban + con hang);
        // neu san pham het sach thi tinh tren tat ca de the van co gia de hien
        BigDecimal thap = null;
        BigDecimal cao = null;
        BigDecimal gocCuaThap = null;
        boolean km = false;
        int ton = 0;
        boolean coBanDuoc = false;
        for (BienTheBanDTO b : bienThe) {
            if (coTheBan(b)) {
                coBanDuoc = true;
            }
        }
        for (BienTheBanDTO b : bienThe) {
            if (coBanDuoc && !coTheBan(b)) {
                continue;
            }
            if (thap == null || b.getGiaBan().compareTo(thap) < 0) {
                thap = b.getGiaBan();
                gocCuaThap = b.getGiaGoc();
            }
            if (cao == null || b.getGiaBan().compareTo(cao) > 0) {
                cao = b.getGiaBan();
            }
            if (b.getGiaBan().compareTo(b.getGiaGoc()) < 0) {
                km = true;
            }
        }
        for (BienTheBanDTO b : bienThe) {
            if ("DANG_BAN".equals(b.getTrangThai())) {
                ton += b.getSoLuongTon();
            }
        }
        this.giaThapNhat = thap;
        this.giaCaoNhat = cao;
        this.giaGocCuaGiaThap = gocCuaThap;
        this.tongTon = ton;
        this.coKhuyenMai = km;
    }

    /** Bien the nay ban duoc ngay khong: dang ban va con hang. */
    private static boolean coTheBan(BienTheBanDTO b) {
        return "DANG_BAN".equals(b.getTrangThai()) && b.getSoLuongTon() > 0;
    }

    public Long getMaSanPham() {
        return maSanPham;
    }

    public List<BienTheBanDTO> getBienThe() {
        return bienThe;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder("{\"id\":").append(maSanPham)
                .append(",\"ten\":").append(JsonUtil.chuoi(tenSanPham))
                .append(",\"hieu\":").append(JsonUtil.chuoi(thuongHieu))
                .append(",\"anh\":").append(JsonUtil.chuoi(anh))
                .append(",\"giaTu\":").append(JsonUtil.so(giaThapNhat))
                .append(",\"giaDen\":").append(JsonUtil.so(giaCaoNhat))
                .append(",\"giaGoc\":").append(JsonUtil.so(giaGocCuaGiaThap))
                .append(",\"ton\":").append(tongTon)
                .append(",\"km\":").append(coKhuyenMai)
                .append(",\"bt\":[");
        for (int i = 0; i < bienThe.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(bienThe.get(i).toJson());
        }
        return sb.append("]}").toString();
    }
}
