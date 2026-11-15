package dto;

import entity.PhieuGiamGia;
import lombok.Getter;

import java.math.BigDecimal;

/** Ket qua kiem tra phieu giam gia: hop le thi co soTienGiam, khong hop le thi co lyDoTuChoi. */
@Getter
public class KetQuaPhieuDTO {
    private final boolean hopLe;
    private final BigDecimal soTienGiam;
    private final String lyDoTuChoi;
    private final PhieuGiamGia phieu;

    private KetQuaPhieuDTO(boolean hopLe, BigDecimal soTienGiam, String lyDoTuChoi, PhieuGiamGia phieu) {
        this.hopLe = hopLe; this.soTienGiam = soTienGiam; this.lyDoTuChoi = lyDoTuChoi; this.phieu = phieu;
    }
    public static KetQuaPhieuDTO hopLe(PhieuGiamGia phieu, BigDecimal soTienGiam) {
        return new KetQuaPhieuDTO(true, soTienGiam, null, phieu);
    }
    public static KetQuaPhieuDTO tuChoi(String lyDo) {
        return new KetQuaPhieuDTO(false, BigDecimal.ZERO, lyDo, null);
    }
}
