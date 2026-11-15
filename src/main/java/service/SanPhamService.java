package service;

import dto.PhanTrang;
import dto.SanPhamDongDTO;
import dto.SanPhamLocDTO;
import entity.DanhMuc;
import entity.SanPham;
import entity.ThuongHieu;
import exception.NghiepVuException;
import repository.DanhMucRepository;
import repository.SanPhamRepository;
import repository.ThuongHieuRepository;
import util.GiaoDich;
import util.SlugUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Nghiep vu SAN PHAM (M3): tim kiem + loc + sap xep + phan trang, them / sua, an hien, ngung kinh doanh, xoa.
 * Bien the xem ChiTietSanPhamService, hinh anh xem HinhAnhSanPhamService, ton kho xem TonKhoService.
 */
public class SanPhamService {

    private final SanPhamRepository repo = new SanPhamRepository();
    private final DanhMucRepository danhMucRepo = new DanhMucRepository();
    private final ThuongHieuRepository thuongHieuRepo = new ThuongHieuRepository();

    // ---------------- TIM KIEM / LOC / SAP XEP / PHAN TRANG ----------------
    public PhanTrang<SanPhamDongDTO> timKiem(SanPhamLocDTO loc) {
        // buoc 1: lay ID cac san pham thoa moi dieu kien loc (da sap xep theo ten / moi nhat)
        List<Long> ids = repo.timIdTheoBoLoc(loc);

        // buoc 2: tong hop gia / ton theo bien the cua cac san pham do
        final Map<Long, Object[]> tongHop = new HashMap<Long, Object[]>();
        for (Object[] r : repo.tongHopBienThe(ids)) tongHop.put((Long) r[0], r);

        // note code nhu nay nhe ban: sap xep theo GIA lam o day vi gia nam o bang ChiTietSanPham (lay gia thap nhat cua cac bien the)
        if ("gia_tang".equals(loc.getSapXep()) || "gia_giam".equals(loc.getSapXep())) {
            final boolean tang = "gia_tang".equals(loc.getSapXep());
            Collections.sort(ids, (a, b) -> {
                BigDecimal ga = giaThapNhat(tongHop.get(a));
                BigDecimal gb = giaThapNhat(tongHop.get(b));
                if (ga == null && gb == null) return 0;
                if (ga == null) return 1;           // san pham chua co bien the xuong cuoi
                if (gb == null) return -1;
                return tang ? ga.compareTo(gb) : gb.compareTo(ga);
            });
        }

        // buoc 3: cat trang
        int tongSo = ids.size();
        int tu = Math.min((loc.getTrang() - 1) * loc.getKichThuoc(), tongSo);
        int den = Math.min(tu + loc.getKichThuoc(), tongSo);
        List<Long> idTrang = new ArrayList<Long>(ids.subList(tu, den));

        // buoc 4: nap san pham + anh chinh cua RIENG trang nay, ghep thanh tung dong
        Map<Long, SanPham> sanPhamTheoId = new HashMap<Long, SanPham>();
        for (SanPham sp : repo.timTheoDanhSachId(idTrang)) sanPhamTheoId.put(sp.getMaSanPham(), sp);
        Map<Long, String> anhTheoId = new HashMap<Long, String>();
        for (Object[] r : new repository.HinhAnhSanPhamRepository().layAnhChinhNhieuSanPham(idTrang)) {
            anhTheoId.put((Long) r[0], (String) r[1]);
        }

        List<SanPhamDongDTO> dong = new ArrayList<SanPhamDongDTO>();
        for (Long id : idTrang) {
            Object[] t = tongHop.get(id);
            String anh = anhTheoId.get(id);
            dong.add(new SanPhamDongDTO(
                    sanPhamTheoId.get(id),
                    t == null ? null : (BigDecimal) t[1],
                    t == null ? null : (BigDecimal) t[2],
                    t == null ? 0 : ((Number) t[3]).longValue(),
                    t == null ? 0 : ((Number) t[4]).longValue(),
                    anh == null ? HinhAnhSanPhamService.ANH_MAC_DINH : anh));   // chua co anh -> anh mac dinh
        }
        return new PhanTrang<SanPhamDongDTO>(dong, loc.getTrang(), loc.getKichThuoc(), tongSo);
    }

    private BigDecimal giaThapNhat(Object[] tongHop) {
        return tongHop == null ? null : (BigDecimal) tongHop[1];
    }

    public SanPham layTheoId(Long id) {
        return id == null ? null : repo.timTheoId(id);
    }

    // ---------------- THEM / SUA ----------------
    /**
     * Them moi (id = null) hoac sua (id != null). Sai nghiep vu thi nem NghiepVuException kem thong bao tieng Viet.
     * maNguoiTao = nhan vien dang dang nhap (chi ghi khi them moi).
     */
    public SanPham luu(Long id, String ten, Long maDanhMuc, Long maThuongHieu, String loai, String moTaNgan, String moTa,
                       boolean dangHienThi, Long maNguoiTao) {
        ten = ten == null ? "" : ten.trim();
        moTaNgan = moTaNgan == null ? "" : moTaNgan.trim();
        moTa = moTa == null ? "" : moTa.trim();
        loai = loai == null ? "" : loai.trim();

        // ---- 1. kiem tra du lieu nhap (theo do dai cot trong SQL) ----
        if (ten.isEmpty()) throw new NghiepVuException("Tên sản phẩm không được để trống");
        if (ten.length() > 200) throw new NghiepVuException("Tên sản phẩm tối đa 200 ký tự");
        if (moTaNgan.length() > 500) throw new NghiepVuException("Mô tả ngắn tối đa 500 ký tự");
        // note code nhu nay nhe ban: 4 loai theo CHECK cua DB: VOT, BONG, PHU_KIEN, TUI
        if (!"VOT".equals(loai) && !"BONG".equals(loai) && !"PHU_KIEN".equals(loai) && !"TUI".equals(loai)) {
            throw new NghiepVuException("Loại sản phẩm phải là Vợt, Bóng, Phụ kiện hoặc Túi");
        }
        if (maDanhMuc == null) throw new NghiepVuException("Vui lòng chọn danh mục");
        if (maThuongHieu == null) throw new NghiepVuException("Vui lòng chọn thương hiệu");

        // ---- 2. danh muc + thuong hieu phai ton tai va dang HOAT_DONG ----
        DanhMuc dm = danhMucRepo.timTheoId(maDanhMuc);
        if (dm == null || !"HOAT_DONG".equals(dm.getTrangThai())) throw new NghiepVuException("Danh mục không hợp lệ hoặc đã ngừng hoạt động");
        ThuongHieu th = thuongHieuRepo.timTheoId(maThuongHieu);
        if (th == null || !"HOAT_DONG".equals(th.getTrangThai())) throw new NghiepVuException("Thương hiệu không hợp lệ hoặc đã ngừng hoạt động");

        // ---- 3. duong dan (slug) tao tu ten, khong duoc trung ----
        String slug = SlugUtil.taoSlug(ten);
        if (slug.isEmpty()) throw new NghiepVuException("Tên sản phẩm phải có chữ hoặc số");
        if (slug.length() > 220) slug = slug.substring(0, 220);
        if (repo.daTonTai("duongDan", slug, id)) throw new NghiepVuException("Đã có sản phẩm trùng tên này");

        // ---- 4. luu ----
        SanPham sp = new SanPham();
        if (id != null) {
            sp = repo.timTheoId(id);
            if (sp == null) throw new NghiepVuException("Không tìm thấy sản phẩm");
        } else {
            sp.setMaNguoiTao(maNguoiTao);
        }
        sp.setTenSanPham(ten);
        sp.setDuongDan(slug);
        sp.setDanhMuc(dm);
        sp.setThuongHieu(th);
        sp.setLoaiSanPham(loai);
        sp.setMoTaNgan(moTaNgan.isEmpty() ? null : moTaNgan);
        sp.setMoTa(moTa.isEmpty() ? null : moTa);
        sp.setDangHienThi(dangHienThi);
        if (id == null) repo.luu(sp); else repo.capNhat(sp);
        return sp;
    }

    // ---------------- AN / HIEN, NGUNG KINH DOANH, XOA ----------------
    /** An hoac hien san pham tren web (khong dong vao cac bien the). */
    public void doiHienThi(Long id) {
        SanPham sp = repo.timTheoId(id);
        if (sp == null) throw new NghiepVuException("Không tìm thấy sản phẩm");
        sp.setDangHienThi(!Boolean.TRUE.equals(sp.getDangHienThi()));
        repo.capNhat(sp);
    }

    // note code nhu nay nhe ban: "ngung kinh doanh" = an san pham + chuyen TAT CA bien the sang NGUNG_BAN, lam chung 1 transaction (loi thi rollback het)
    public void ngungKinhDoanh(final Long id) {
        if (repo.timTheoId(id) == null) throw new NghiepVuException("Không tìm thấy sản phẩm");
        GiaoDich.chay(s -> {
            s.createQuery("update SanPham x set x.dangHienThi = false, x.ngayCapNhat = :bayGio where x.maSanPham = :id")
                    .setParameter("bayGio", LocalDateTime.now()).setParameter("id", id).executeUpdate();
            s.createQuery("update ChiTietSanPham c set c.trangThai = 'NGUNG_BAN', c.ngayCapNhat = :bayGio where c.sanPham.maSanPham = :id")
                    .setParameter("bayGio", LocalDateTime.now()).setParameter("id", id).executeUpdate();
            return null;
        });
    }

    // note code nhu nay nhe ban: chi xoa cung duoc san pham CHUA co bien the nao (bien the co the da nam trong don hang). Con bien the thi dung ngungKinhDoanh
    public void xoa(final Long id) {
        if (repo.timTheoId(id) == null) throw new NghiepVuException("Không tìm thấy sản phẩm");
        long soBienThe = repo.demBienThe(id);
        if (soBienThe > 0) {
            throw new NghiepVuException("Sản phẩm còn " + soBienThe + " biến thể, hãy xóa biến thể hoặc dùng \"Ngừng kinh doanh\"");
        }
        GiaoDich.chay(s -> {
            s.createQuery("delete from HinhAnhSanPham h where h.maSanPham = :id").setParameter("id", id).executeUpdate();
            s.createQuery("delete from SanPham x where x.maSanPham = :id").setParameter("id", id).executeUpdate();
            return null;
        });
    }
}
