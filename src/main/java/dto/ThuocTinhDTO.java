package dto;

import java.time.LocalDateTime;

/** Dung chung cho 4 thuoc tinh: mau sac, chat lieu, khoi luong, chu vi tay cam. */
public class ThuocTinhDTO {
    private final Long ma;
    private final String giaTri;        // chu hien thi, vd "225 g"
    private final String giaTriGoc;     // gia tri tho de dua vao o nhap khi sua, vd "225"
    private final String trangThai;
    private final LocalDateTime ngayTao;

    public ThuocTinhDTO(Long ma, String giaTri, String giaTriGoc, String trangThai, LocalDateTime ngayTao) {
        this.ma = ma; this.giaTri = giaTri; this.giaTriGoc = giaTriGoc; this.trangThai = trangThai; this.ngayTao = ngayTao;
    }
    public Long getMa() { return ma; }
    public String getGiaTri() { return giaTri; }
    public String getGiaTriGoc() { return giaTriGoc; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getNgayTao() { return ngayTao; }
}
