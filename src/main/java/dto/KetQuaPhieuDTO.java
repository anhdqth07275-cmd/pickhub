package dto;

import entity.PhieuGiamGia;

import java.math.BigDecimal;

/**
 * Ket qua kiem tra 1 phieu giam gia (M5 / M6 dung).
 * hopLe = true  -> soTienGiam la so tien duoc giam, phieu la phieu da tim thay.
 * hopLe = false -> lyDoTuChoi la thong bao tieng Viet hien thi cho khach.
 */
public class KetQuaPhieuDTO {
    private final boolean hopLe;
    private final BigDecimal soTienGiam;
    private final String lyDoTuChoi;
    private final PhieuGiamGia phieu;

    private KetQuaPhieuDTO(boolean hopLe, BigDecimal soTienGiam, String lyDoTuChoi, PhieuGiamGia phieu) {
        this.hopLe = hopLe;
        this.soTienGiam = soTienGiam;
        this.lyDoTuChoi = lyDoTuChoi;
        this.phieu = phieu;
    }

    public static KetQuaPhieuDTO thanhCong(PhieuGiamGia phieu, BigDecimal soTienGiam) {
        return new KetQuaPhieuDTO(true, soTienGiam, null, phieu);
    }

    public static KetQuaPhieuDTO tuChoi(String lyDo) {
        return new KetQuaPhieuDTO(false, BigDecimal.ZERO, lyDo, null);
    }

    public boolean isHopLe() { return hopLe; }
    public BigDecimal getSoTienGiam() { return soTienGiam; }
    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public PhieuGiamGia getPhieu() { return phieu; }
}
