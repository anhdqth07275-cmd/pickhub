package dto;

import lombok.Getter;
import lombok.Setter;

/** Du lieu form dat hang (dia chi 4 cap + nguoi nhan). */
@Getter
@Setter
public class ThongTinGiaoHangDTO {
    private String tenNguoiNhan = "";
    private String soDienThoaiNguoiNhan = "";
    private String tinhThanh = "";
    private String quanHuyen = "";
    private String phuongXa = "";
    private String soNhaDuong = "";
    private String ghiChu = "";
}
