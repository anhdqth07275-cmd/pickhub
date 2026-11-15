package service;

import dto.BoLoc;
import dto.PhanTrang;
import entity.ChiTietSanPham;
import exception.NghiepVuException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import repository.ChiTietSanPhamRepository;
import util.GiaoDich;

import java.util.List;

/**
 * Quan ly TON KHO (M3). Ton kho nam trong cot ChiTietSanPham.soLuongTon - moi bien the 1 so ton.
 * M5 (dat hang) goi truTon / hoanTon de tru - hoan ton, M6 (huy don) goi hoanTon.
 */
public class TonKhoService {

    private final ChiTietSanPhamRepository repo = new ChiTietSanPhamRepository();

    // ---------------- TRANG THAI TU DONG ----------------
    // note code nhu nay nhe ban: ton = 0 -> HET_HANG, co hang lai -> DANG_BAN. NGUNG_BAN la nguoi dung tu chon nen KHONG tu doi
    public static void dongBoTrangThai(ChiTietSanPham ct) {
        if ("NGUNG_BAN".equals(ct.getTrangThai())) return;
        ct.setTrangThai(ct.getSoLuongTon() != null && ct.getSoLuongTon() > 0 ? "DANG_BAN" : "HET_HANG");
    }

    // ---------------- MAN HINH TON KHO ----------------
    /** Danh sach ton kho: tim theo ma hang / ten san pham, loc tinh trang (HET / SAP_HET / CON), it hang len truoc. */
    public PhanTrang<ChiTietSanPham> timKiem(String tuKhoa, String tinhTrang, int trang) {
        BoLoc loc = new BoLoc().cotTim("maHang,sanPham.tenSanPham").tuKhoa(tuKhoa).trang(trang).sapXep("x.soLuongTon asc");
        if ("HET".equals(tinhTrang)) loc.dieuKienThem("x.soLuongTon = 0");
        else if ("SAP_HET".equals(tinhTrang)) loc.dieuKienThem("x.soLuongTon > 0 and x.soLuongTon <= x.nguongCanhBaoTonKho");
        else if ("CON".equals(tinhTrang)) loc.dieuKienThem("x.soLuongTon > x.nguongCanhBaoTonKho");
        return repo.timKiem(loc);
    }

    /** Dat lai so ton (kiem ke kho). So moi khong duoc am. */
    public void capNhatTon(final Long maChiTietSanPham, final int soLuongMoi) {
        if (soLuongMoi < 0) throw new NghiepVuException("Số lượng tồn không được âm");
        GiaoDich.chay(s -> {
            ChiTietSanPham ct = layVaKhoa(s, maChiTietSanPham);
            ct.setSoLuongTon(soLuongMoi);
            dongBoTrangThai(ct);
            return null;
        });
    }

    /** Nhap them hang vao kho. */
    public void nhapThem(final Long maChiTietSanPham, final int soLuongThem) {
        if (soLuongThem <= 0) throw new NghiepVuException("Số lượng nhập thêm phải lớn hơn 0");
        GiaoDich.chay(s -> {
            hoanTon(s, maChiTietSanPham, soLuongThem);
            return null;
        });
    }

    public List<ChiTietSanPham> laySapHet(int soLuong) {
        return repo.laySapHet(soLuong);
    }

    public long demSapHet() {
        return repo.demSapHet();
    }

    // ---------------- API CHO M5 / M6 ----------------
    /** M5 hoi nhanh: con du hang khong (chi de hien thi, luc dat hang van phai kiem tra lai trong truTon). */
    public boolean kiemTraDuHang(Long maChiTietSanPham, int soLuong) {
        ChiTietSanPham ct = repo.timTheoId(maChiTietSanPham);
        return ct != null && !"NGUNG_BAN".equals(ct.getTrangThai()) && ct.getSoLuongTon() >= soLuong;
    }

    /**
     * M5 goi luc DAT HANG, truyen Session cua giao dich dat hang -> tru ton cung 1 transaction voi tao hoa don
     * (loi o buoc nao thi rollback het, khong bi tru ton ma khong co don).
     */
    public void truTon(Session session, Long maChiTietSanPham, int soLuong) {
        if (soLuong <= 0) throw new NghiepVuException("Số lượng phải lớn hơn 0");
        ChiTietSanPham ct = layVaKhoa(session, maChiTietSanPham);
        if ("NGUNG_BAN".equals(ct.getTrangThai())) {
            throw new NghiepVuException("Sản phẩm " + ct.getMaHang() + " đã ngừng bán");
        }
        // note code nhu nay nhe ban: kiem tra lai ton NGAY LUC DAT (khoa dong de 2 nguoi mua cung luc khong ban lo)
        if (ct.getSoLuongTon() < soLuong) {
            throw new NghiepVuException("Sản phẩm " + ct.getMaHang() + " chỉ còn " + ct.getSoLuongTon() + " cái");
        }
        ct.setSoLuongTon(ct.getSoLuongTon() - soLuong);
        dongBoTrangThai(ct);
    }

    /** M6 goi khi huy don (hoan hang vao kho); cung nhan Session de nam chung transaction. */
    public void hoanTon(Session session, Long maChiTietSanPham, int soLuong) {
        if (soLuong <= 0) throw new NghiepVuException("Số lượng phải lớn hơn 0");
        ChiTietSanPham ct = layVaKhoa(session, maChiTietSanPham);
        ct.setSoLuongTon(ct.getSoLuongTon() + soLuong);
        dongBoTrangThai(ct);
    }

    // note code nhu nay nhe ban: PESSIMISTIC_WRITE = khoa dong trong SQL Server den khi commit, nguoi khac phai doi
    private ChiTietSanPham layVaKhoa(Session session, Long id) {
        ChiTietSanPham ct = session.get(ChiTietSanPham.class, id, LockMode.PESSIMISTIC_WRITE);
        if (ct == null) throw new NghiepVuException("Không tìm thấy biến thể sản phẩm");
        return ct;
    }
}
