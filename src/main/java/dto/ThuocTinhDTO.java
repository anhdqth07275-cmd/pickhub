package dto;

import java.time.LocalDateTime;

/** Dung chung cho 4 thuoc tinh: mau sac, chat lieu, khoi luong, chu vi tay cam. */
public class ThuocTinhDTO {
    private final Long ma;
    private final String giaTri;
    private final String trangThai;
    private final LocalDateTime ngayTao;

    public ThuocTinhDTO(Long ma, String giaTri, String trangThai, LocalDateTime ngayTao) {
        this.ma = ma; this.giaTri = giaTri; this.trangThai = trangThai; this.ngayTao = ngayTao;
    }
    public Long getMa() { return ma; }
    public String getGiaTri() { return giaTri; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getNgayTao() { return ngayTao; }
}
