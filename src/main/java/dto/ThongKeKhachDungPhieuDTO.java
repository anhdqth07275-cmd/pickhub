package dto;

/**
 * 1 dong thong ke: khach nay da dung phieu giam gia X bao nhieu lan / toi da bao nhieu lan.
 * Dung o trang /wp-admin/phieu-giam-gia/su-dung?maPhieu=... de hien thi ro gioi han moi khach.
 */
public class ThongKeKhachDungPhieuDTO {
    private final Long maKhachHang;
    private final String hoTen;
    private final String thuDienTu;
    private final long soLuotDaDung;     // chi dem luot DA_SU_DUNG (luot da hoan khi huy don thi khong tinh)
    private final int gioiHanMoiNguoi;   // PhieuGiamGia.gioiHanMoiNguoi

    public ThongKeKhachDungPhieuDTO(Long maKhachHang, String hoTen, String thuDienTu, long soLuotDaDung, int gioiHanMoiNguoi) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.thuDienTu = thuDienTu;
        this.soLuotDaDung = soLuotDaDung;
        this.gioiHanMoiNguoi = gioiHanMoiNguoi;
    }

    public Long getMaKhachHang() { return maKhachHang; }
    public String getHoTen() { return hoTen; }
    public String getThuDienTu() { return thuDienTu; }
    public long getSoLuotDaDung() { return soLuotDaDung; }
    public int getGioiHanMoiNguoi() { return gioiHanMoiNguoi; }

    // note code nhu nay nhe ban: so luot con lai, khong bao gio am (du lieu cu co the dung vuot)
    public long getConLai() { return Math.max(0, gioiHanMoiNguoi - soLuotDaDung); }

    // da dung het luot cho phep -> JSP to mau do
    public boolean isHetLuot() { return soLuotDaDung >= gioiHanMoiNguoi; }
}
