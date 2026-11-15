package enums;

/**
 * 3 vai tro cua nhan vien (cot NhanVien.vaiTro). Cap do cang cao quyen cang lon.
 * QUAN_TRI (3) > QUAN_LY (2) > NHAN_VIEN (1)
 */
public enum VaiTro {
    NHAN_VIEN(1),
    QUAN_LY(2),
    QUAN_TRI(3);

    private final int capDo;

    VaiTro(int capDo) {
        this.capDo = capDo;
    }

    public int getCapDo() {
        return capDo;
    }

    // note code nhu nay nhe ban: doi chuoi trong DB ("QUAN_LY") ra enum; chuoi la / null thi tra null => khong co quyen gi
    public static VaiTro tuMa(String ma) {
        for (VaiTro v : values()) {
            if (v.name().equals(ma)) return v;
        }
        return null;
    }

    /** true neu vai tro nay bang hoac cao hon vai tro "toiThieu" */
    public boolean tuCapDo(VaiTro toiThieu) {
        return this.capDo >= toiThieu.capDo;
    }
}
