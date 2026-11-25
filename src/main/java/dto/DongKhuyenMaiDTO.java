package dto;

import entity.ChiTietSanPham;

/**
 * 1 dong san pham (bien the) trong bang "Tich chon san pham ap dung khuyen mai" cua popup khuyen mai.
 *
 * note code nhu nay nhe ban: cac o nguoi dung nhap (loaiGiam, giaTriGiam, soLuongGioiHan) de dang CHUOI, khong doi sang so o day,
 * de khi luu bi loi thi hien lai DUNG nhung gi nguoi dung vua go (khong mat). Viec doi sang so + kiem tra nam o KhuyenMaiService.
 */
public class DongKhuyenMaiDTO {

    private ChiTietSanPham bienThe;          // bien the san pham (co san pham, mau, khoi luong... de hien thi)
    private boolean chon;                    // true = da tich chon
    private Long maDong;                     // ma ChiTietChuongTrinhKhuyenMai neu dong da co trong chuong trinh, moi thi null
    private String loaiGiam = "PHAN_TRAM";   // PHAN_TRAM | SO_TIEN
    private String giaTriGiam = "";
    private String soLuongGioiHan = "";      // rong = khong gioi han
    private int soLuongDaBan;                // chi co y nghia voi dong da ton tai

    public DongKhuyenMaiDTO() {
    }

    public DongKhuyenMaiDTO(ChiTietSanPham bienThe) {
        this.bienThe = bienThe;
    }

    /** Mo ta ngan cua bien the de hien duoi ten san pham, vd "Đen · 225 g · 4.125 inch". */
    public String getMoTaBienThe() {
        StringBuilder sb = new StringBuilder();
        if (bienThe.getMauSac() != null) sb.append(bienThe.getMauSac().getTenMauSac());
        if (bienThe.getKhoiLuong() != null) append(sb, bienThe.getKhoiLuong().getKhoiLuongGram() + " g");
        if (bienThe.getChatLieu() != null) append(sb, bienThe.getChatLieu().getTenChatLieu());
        if (bienThe.getChuViTayCam() != null) append(sb, bienThe.getChuViTayCam().getChuViInch() + " inch");
        return sb.toString();
    }

    private void append(StringBuilder sb, String s) {
        if (sb.length() > 0) sb.append(" · ");
        sb.append(s);
    }

    /** Chuoi dung cho o tim kiem phia trinh duyet (ma hang + ten + mo ta), da viet thuong. */
    public String getChuoiTim() {
        return (bienThe.getMaHang() + " " + bienThe.getSanPham().getTenSanPham() + " " + getMoTaBienThe()).toLowerCase();
    }

    public ChiTietSanPham getBienThe() { return bienThe; }
    public void setBienThe(ChiTietSanPham bienThe) { this.bienThe = bienThe; }
    public boolean isChon() { return chon; }
    public void setChon(boolean chon) { this.chon = chon; }
    public Long getMaDong() { return maDong; }
    public void setMaDong(Long maDong) { this.maDong = maDong; }
    public String getLoaiGiam() { return loaiGiam; }
    public void setLoaiGiam(String loaiGiam) { this.loaiGiam = loaiGiam; }
    public String getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(String giaTriGiam) { this.giaTriGiam = giaTriGiam; }
    public String getSoLuongGioiHan() { return soLuongGioiHan; }
    public void setSoLuongGioiHan(String soLuongGioiHan) { this.soLuongGioiHan = soLuongGioiHan; }
    public int getSoLuongDaBan() { return soLuongDaBan; }
    public void setSoLuongDaBan(int soLuongDaBan) { this.soLuongDaBan = soLuongDaBan; }
}
