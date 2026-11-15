package dto;

import java.time.LocalDate;

/**
 * Bo loc man hinh "Lich su trang thai don hang" (module M6).
 */
public class LichSuLocDTO {
    public String tuKhoa = "";          // ma tra cuu cua don (vd HD2601140001)
    public String denTrangThai = "";    // rong = tat ca trang thai
    public String nguoiThucHien = "";   // "" = tat ca, "NHAN_VIEN" = do nhan vien, "HE_THONG" = do khach / he thong
    public LocalDate tuNgay;            // null = khong loc
    public LocalDate denNgay;           // null = khong loc
    public int trang = 1;
    public int kichThuoc = 15;
}
