package util;

import enums.VaiTro;

/**
 * NOI DUY NHAT chua ma tran phan quyen cua /wp-admin.
 * - WpAdminFilter goi coQuyen(vaiTro, duongDan, phuongThuc) de chan / cho di qua.
 * - header.jspf goi ${ph:coQuyen(vaiTro, duongDan)} (khai bao trong pickhub.tld) de an / hien menu.
 * Muon doi quyen chi sua o day, khong phai sua o 2 cho.
 *
 * Ma tran hien tai:
 *   QUAN_TRI  : lam duoc moi viec
 *   QUAN_LY   : moi viec (bao gom khuyen mai, phieu giam gia, nhan vien)
 *   NHAN_VIEN : KHONG vao duoc /wp-admin (xem duocVaoAdmin) - chi lam viec o man hinh ban hang POS (/pos)
 */
public class PhanQuyenUtil {

    // note code nhu nay nhe ban: khu vuc bi han che = {duong dan, vai tro thap nhat duoc vao}
    private static final String[][] KHU_VUC_HAN_CHE = {
            {"/wp-admin/khuyen-mai", "QUAN_LY"},
            {"/wp-admin/phieu-giam-gia", "QUAN_LY"},
            {"/wp-admin/nhan-vien", "QUAN_LY"}
    };

    // note code nhu nay nhe ban: NHAN_VIEN chi duoc thao tac ghi (POST...) o cac khu vuc nay
    private static final String[] NHAN_VIEN_DUOC_GHI = {
            "/wp-admin/don-hang",
            "/wp-admin/tai-khoan",
            "/wp-admin/ton-kho"
    };

    /**
     * YEU CAU PHAN QUYEN: chi QUAN_LY va QUAN_TRI moi duoc vao khu vuc /wp-admin.
     * WpAdminFilter goi ham nay TRUOC khi xet tung trang; sai thi day nhan vien sang POS.
     */
    public static boolean duocVaoAdmin(String vaiTro) {
        VaiTro vt = VaiTro.tuMa(vaiTro);
        // note code nhu nay nhe ban: vai tro la / null -> khong cho vao; con lai phai tu cap QUAN_LY tro len (QUAN_LY, QUAN_TRI)
        return vt != null && vt.tuCapDo(VaiTro.QUAN_LY);
    }

    /** Dung cho JSP: nguoi nay co vao (xem) duoc trang nay khong? */
    public static boolean coQuyen(String vaiTro, String duongDan) {
        return coQuyen(vaiTro, duongDan, "GET");
    }

    /**
     * Dung cho filter.
     * vaiTro     : chuoi trong session (QUAN_TRI / QUAN_LY / NHAN_VIEN)
     * duongDan   : duong dan SAU context path, da chuan hoa (vd "/wp-admin/nhan-vien")
     * phuongThuc : GET / POST ...
     */
    public static boolean coQuyen(String vaiTro, String duongDan, String phuongThuc) {
        VaiTro vt = VaiTro.tuMa(vaiTro);
        if (vt == null) return false;                       // vai tro la / rong => khong cho vao
        if (vt == VaiTro.QUAN_TRI) return true;             // quan tri lam duoc tat ca

        // 1. khu vuc han che: so sanh cap do vai tro voi vai tro toi thieu cua khu vuc
        for (String[] dong : KHU_VUC_HAN_CHE) {
            if (thuocKhuVuc(duongDan, dong[0])) {
                return vt.tuCapDo(VaiTro.tuMa(dong[1]));
            }
        }

        // 2. NHAN_VIEN: GET / HEAD thi xem thoai mai, con lai (POST...) chi duoc o khu vuc cho phep ghi
        if (vt == VaiTro.NHAN_VIEN && !laDocDuLieu(phuongThuc)) {
            for (String khuVuc : NHAN_VIEN_DUOC_GHI) {
                if (thuocKhuVuc(duongDan, khuVuc)) return true;
            }
            return false;
        }
        return true;
    }

    /** Quản lý chỉ được tác động lên nhân viên; quản trị viên được quản lý mọi cấp. */
    public static boolean duocQuanLyNhanVien(String vaiTroNguoiDung, String vaiTroMucTieu) {
        if ("QUAN_TRI".equals(vaiTroNguoiDung)) return true;
        return "QUAN_LY".equals(vaiTroNguoiDung) && "NHAN_VIEN".equals(vaiTroMucTieu);
    }

    /** Chỉ quản trị viên được cấp hoặc thay đổi vai trò QUAN_TRI / QUAN_LY. */
    public static boolean duocGanVaiTro(String vaiTroNguoiDung, String vaiTroMoi) {
        if ("QUAN_TRI".equals(vaiTroNguoiDung)) return true;
        return false;
    }

    private static boolean laDocDuLieu(String phuongThuc) {
        return "GET".equalsIgnoreCase(phuongThuc) || "HEAD".equalsIgnoreCase(phuongThuc);
    }

    // "/wp-admin/don-hang" va "/wp-admin/don-hang/chi-tiet" thuoc khu vuc "/wp-admin/don-hang"; "/wp-admin/don-hang-abc" thi khong
    private static boolean thuocKhuVuc(String duongDan, String khuVuc) {
        return duongDan.equals(khuVuc) || duongDan.startsWith(khuVuc + "/");
    }
}
