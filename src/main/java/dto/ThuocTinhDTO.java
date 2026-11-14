package dto;

import java.time.LocalDateTime;

/** Dung chung cho 4 thuoc tinh: mau sac, chat lieu, khoi luong, chu vi tay cam. */
public class ThuocTinhDTO {
    private final Long ma;
    private final String ten;        // chu hien thi, vd "225 g"
    private final String giaTri;     // gia tri goc, vd "225" (dung do vao form sua)
    private final String trangThai;
    private final LocalDateTime ngayTao;

    // 5 tham so: (ma, ten hien thi, gia tri goc, trang thai, ngay tao)
    public ThuocTinhDTO(Long ma, String ten, String giaTri, String trangThai, LocalDateTime ngayTao) {
        this.ma = ma; this.ten = ten; this.giaTri = giaTri; this.trangThai = trangThai; this.ngayTao = ngayTao;
    }

    // 4 tham so (ban dau): ten hien thi = gia tri
    public ThuocTinhDTO(Long ma, String giaTri, String trangThai, LocalDateTime ngayTao) {
        this(ma, giaTri, giaTri, trangThai, ngayTao);
    }

    public Long getMa() { return ma; }
    public String getTen() { return ten; }
    public String getGiaTri() { return giaTri; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getNgayTao() { return ngayTao; }
}