package dto;

import entity.SanPham;

import java.math.BigDecimal;

/**
 * 1 dong trong bang danh sach san pham: san pham + thong tin tong hop tu cac bien the.
 */
public class SanPhamDongDTO {
    private SanPham sanPham;
    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;
    private long tongTon;
    private long soBienThe;
    private String anhChinh;

    public SanPhamDongDTO(SanPham sanPham, BigDecimal giaThapNhat, BigDecimal giaCaoNhat, long tongTon, long soBienThe, String anhChinh) {
        this.sanPham = sanPham;
        this.giaThapNhat = giaThapNhat;
        this.giaCaoNhat = giaCaoNhat;
        this.tongTon = tongTon;
        this.soBienThe = soBienThe;
        this.anhChinh = anhChinh;
    }

    public SanPham getSanPham() { return sanPham; }
    public BigDecimal getGiaThapNhat() { return giaThapNhat; }
    public BigDecimal getGiaCaoNhat() { return giaCaoNhat; }
    public long getTongTon() { return tongTon; }
    public long getSoBienThe() { return soBienThe; }
    public String getAnhChinh() { return anhChinh; }
}
