package service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Sinh ma tra cuu theo mau du lieu: HD + yyMMdd + ma hoa don 4 chu so (vd HD2601140001). */
public class MaTraCuuGenerator {

    public static String taoMa(Long maHoaDon, LocalDate ngay) {
        // note code nhu nay nhe ban: ma hoa don la duy nhat nen ma tra cuu cung duy nhat
        return "HD" + ngay.format(DateTimeFormatter.ofPattern("yyMMdd")) + String.format("%04d", maHoaDon);
    }
}
