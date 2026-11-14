package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/** Ket qua tinh gia sau khuyen mai cua 1 chi tiet san pham. maChiTietCtkm = null nghia la khong co khuyen mai. */
@Getter
@AllArgsConstructor
public class KetQuaGiaDTO {
    private final BigDecimal giaGoc;
    private final BigDecimal giaSauGiam;
    private final Long maChiTietCtkm;
}
