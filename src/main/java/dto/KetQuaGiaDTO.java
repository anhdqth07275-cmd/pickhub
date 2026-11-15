package dto;

import java.math.BigDecimal;

/**
 * Ket qua tinh gia sau khuyen mai cua 1 chi tiet san pham (M5 / M6 / storefront dung).
 * maChiTietCTKM = null nghia la khong co khuyen mai nao dang ap dung -> giaSauGiam = giaGoc.
 */
public class KetQuaGiaDTO {
    private final BigDecimal giaGoc;
    private final BigDecimal giaSauGiam;
    private final Long maChiTietCTKM;      // ma dong ChiTietChuongTrinhKhuyenMai duoc ap dung (luu vao HoaDonChiTiet)

    public KetQuaGiaDTO(BigDecimal giaGoc, BigDecimal giaSauGiam, Long maChiTietCTKM) {
        this.giaGoc = giaGoc;
        this.giaSauGiam = giaSauGiam;
        this.maChiTietCTKM = maChiTietCTKM;
    }

    public BigDecimal getGiaGoc() { return giaGoc; }
    public BigDecimal getGiaSauGiam() { return giaSauGiam; }
    public Long getMaChiTietCTKM() { return maChiTietCTKM; }

    public boolean isCoKhuyenMai() {
        return maChiTietCTKM != null;
    }

    public BigDecimal getSoTienGiam() {
        return giaGoc.subtract(giaSauGiam);
    }
}
