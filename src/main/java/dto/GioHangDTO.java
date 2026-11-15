package dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Du lieu hien thi gio hang: cac dong + tien + canh bao (doi gia / het hang). */
@Getter
@Setter
public class GioHangDTO {
    private List<DongGioHangDTO> dong = new ArrayList<DongGioHangDTO>();
    private List<String> canhBao = new ArrayList<String>();
    private BigDecimal tamTinh = BigDecimal.ZERO;
    private BigDecimal soTienGiam = BigDecimal.ZERO;
    private BigDecimal tongTien = BigDecimal.ZERO;
    private String maPhieu;          // ma phieu dang ap (null = chua ap)
    private boolean coLoi;           // true neu co dong khong dat hang duoc -> chan nut dat hang

    public boolean isRong() { return dong.isEmpty(); }

    public int getTongSoLuong() {
        int tong = 0;
        for (DongGioHangDTO d : dong) tong += d.getSoLuong();
        return tong;
    }
}
