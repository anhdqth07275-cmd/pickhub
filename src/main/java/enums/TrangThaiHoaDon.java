package enums;

/**
 * Trang thai hoa don - khop 100% voi CHECK CK_HoaDon_trangThai trong SQL.
 * Luong hop le: CHO_XAC_NHAN -> DA_XAC_NHAN -> DANG_GIAO -> HOAN_THANH
 *               CHO_XAC_NHAN / DA_XAC_NHAN -> DA_HUY
 */
public enum TrangThaiHoaDon {
    GIO_HANG, CHO_XAC_NHAN, DA_XAC_NHAN, DANG_GIAO, HOAN_THANH, DA_HUY;

    // note code nhu nay nhe ban: buoc tiep theo tren luong xu ly (null = da ket thuc hoac chua la don hang)
    public TrangThaiHoaDon tiepTheo() {
        switch (this) {
            case CHO_XAC_NHAN: return DA_XAC_NHAN;
            case DA_XAC_NHAN: return DANG_GIAO;
            case DANG_GIAO: return HOAN_THANH;
            default: return null;
        }
    }

    // note code nhu nay nhe ban: kiem tra cho phep doi trang thai (chan chuyen sai luong, vd HOAN_THANH -> CHO_XAC_NHAN)
    public boolean coTheChuyenSang(TrangThaiHoaDon moi) {
        if (moi == tiepTheo()) return true;
        // don dang giao thi khong cho huy
        return moi == DA_HUY && (this == CHO_XAC_NHAN || this == DA_XAC_NHAN);
    }

    public static TrangThaiHoaDon tuChuoi(String ma) {
        try {
            return valueOf(ma);
        } catch (Exception e) {
            return null;
        }
    }
}
