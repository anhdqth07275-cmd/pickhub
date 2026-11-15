package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 1 dong tren man hinh lich su: gop LichSuTrangThaiHoaDon + ma tra cuu cua don (de hien cho de doc).
 */
@Getter
@AllArgsConstructor
public class LichSuDongDTO {
    private final Long maHoaDon;
    private final String maTraCuu;          // null neu la gio hang chua dat
    private final String tuTrangThai;       // null = ban ghi khoi tao
    private final String denTrangThai;
    private final String ghiChu;
    private final String tenNguoiThayDoi;   // null = khach hang / he thong
    private final LocalDateTime ngayThayDoi;
}
