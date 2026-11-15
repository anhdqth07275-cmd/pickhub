package service;

import entity.ChiTietSanPham;
import entity.HinhAnhSanPham;
import entity.SanPham;
import exception.NghiepVuException;
import repository.ChiTietSanPhamRepository;
import repository.HinhAnhSanPhamRepository;
import repository.SanPhamRepository;
import util.GiaoDich;

import java.util.List;

/**
 * Quan ly hinh anh san pham (M3). Luu DUONG DAN anh (link http hoac /duong-dan-trong-web), khong luu file trong DB.
 * Quy tac: moi san pham co toi da 1 anh chinh; anh chinh phai la anh CHUNG (khong gan bien the).
 */
public class HinhAnhSanPhamService {

    // note code nhu nay nhe ban: san pham chua co anh thi hien anh mac dinh nay (nho de 1 file no-image.png trong webapp/assets)
    public static final String ANH_MAC_DINH = "/assets/no-image.png";

    private final HinhAnhSanPhamRepository repo = new HinhAnhSanPhamRepository();
    private final SanPhamRepository sanPhamRepo = new SanPhamRepository();
    private final ChiTietSanPhamRepository chiTietRepo = new ChiTietSanPhamRepository();

    public List<HinhAnhSanPham> layTheoSanPham(Long maSanPham) {
        return repo.layTheoSanPham(maSanPham);
    }

    /** Duong dan anh dai dien (hoac anh mac dinh neu chua co anh nao). */
    public String layAnhChinh(Long maSanPham) {
        HinhAnhSanPham a = repo.layAnhChinh(maSanPham);
        return a == null ? ANH_MAC_DINH : a.getDuongDanAnh();
    }

    // ---------------- THEM ----------------
    public void them(final Long maSanPham, final Long maChiTietSanPham, String duongDan, final boolean laAnhChinh) {
        SanPham sp = sanPhamRepo.timTheoId(maSanPham);
        if (sp == null) throw new NghiepVuException("Không tìm thấy sản phẩm");
        final String url = kiemTraDuongDan(duongDan);
        final String loaiTep = layLoaiTep(url);

        // anh rieng cua bien the: bien the phai thuoc DUNG san pham nay
        if (maChiTietSanPham != null) {
            ChiTietSanPham ct = chiTietRepo.timTheoId(maChiTietSanPham);
            if (ct == null || !ct.getSanPham().getMaSanPham().equals(maSanPham)) {
                throw new NghiepVuException("Biến thể không thuộc sản phẩm này");
            }
        }

        GiaoDich.chay(s -> {
            // note code nhu nay nhe ban: chua co anh chinh nao thi anh CHUNG dau tien tu thanh anh chinh
            long soAnhChinh = s.createQuery("select count(h) from HinhAnhSanPham h where h.maSanPham = :id and h.laAnhChinh = true", Long.class)
                    .setParameter("id", maSanPham).uniqueResult();
            boolean thanhAnhChinh = maChiTietSanPham == null && (laAnhChinh || soAnhChinh == 0);
            if (thanhAnhChinh) {
                // bo co anh chinh cua cac anh cu truoc khi gan anh moi
                s.createQuery("update HinhAnhSanPham h set h.laAnhChinh = false where h.maSanPham = :id")
                        .setParameter("id", maSanPham).executeUpdate();
            }
            Integer thuTuLonNhat = s.createQuery("select max(h.thuTuSapXep) from HinhAnhSanPham h where h.maSanPham = :id", Integer.class)
                    .setParameter("id", maSanPham).uniqueResult();

            HinhAnhSanPham a = new HinhAnhSanPham();
            a.setMaSanPham(maSanPham);
            a.setMaChiTietSanPham(maChiTietSanPham);
            a.setDuongDanAnh(url);
            a.setLoaiTep(loaiTep);
            a.setLaAnhChinh(thanhAnhChinh);
            a.setThuTuSapXep(thuTuLonNhat == null ? 1 : thuTuLonNhat + 1);
            s.persist(a);
            return null;
        });
    }

    // ---------------- SUA / CHON ANH CHINH / XOA ----------------
    public void sua(Long maHinhAnh, String duongDan, Integer thuTu) {
        HinhAnhSanPham a = repo.timTheoId(maHinhAnh);
        if (a == null) throw new NghiepVuException("Không tìm thấy hình ảnh");
        String url = kiemTraDuongDan(duongDan);
        a.setDuongDanAnh(url);
        a.setLoaiTep(layLoaiTep(url));
        if (thuTu != null) a.setThuTuSapXep(thuTu);
        repo.capNhat(a);
    }

    public void datAnhChinh(final Long maHinhAnh) {
        GiaoDich.chay(s -> {
            HinhAnhSanPham a = s.get(HinhAnhSanPham.class, maHinhAnh);
            if (a == null) throw new NghiepVuException("Không tìm thấy hình ảnh");
            if (a.getMaChiTietSanPham() != null) {
                throw new NghiepVuException("Ảnh đại diện phải là ảnh chung của sản phẩm, không phải ảnh của biến thể");
            }
            s.createQuery("update HinhAnhSanPham h set h.laAnhChinh = false where h.maSanPham = :id")
                    .setParameter("id", a.getMaSanPham()).executeUpdate();
            a.setLaAnhChinh(true);
            return null;
        });
    }

    public void xoa(final Long maHinhAnh) {
        GiaoDich.chay(s -> {
            HinhAnhSanPham a = s.get(HinhAnhSanPham.class, maHinhAnh);
            if (a == null) throw new NghiepVuException("Không tìm thấy hình ảnh");
            boolean daLaAnhChinh = Boolean.TRUE.equals(a.getLaAnhChinh());
            Long maSanPham = a.getMaSanPham();
            s.remove(a);
            s.flush();
            // note code nhu nay nhe ban: xoa dung anh chinh thi chon anh chung con lai (thu tu nho nhat) lam anh chinh moi
            if (daLaAnhChinh) {
                List<HinhAnhSanPham> con = s.createQuery("from HinhAnhSanPham h where h.maSanPham = :id and h.maChiTietSanPham is null order by h.thuTuSapXep", HinhAnhSanPham.class)
                        .setParameter("id", maSanPham).setMaxResults(1).list();
                if (!con.isEmpty()) con.get(0).setLaAnhChinh(true);
            }
            return null;
        });
    }

    // ---------------- KIEM TRA ----------------
    // note code nhu nay nhe ban: chi nhan link http(s) hoac duong dan bat dau bang "/", duoi file phai la jpg / jpeg / png / webp
    private String kiemTraDuongDan(String duongDan) {
        String url = duongDan == null ? "" : duongDan.trim();
        if (url.isEmpty()) throw new NghiepVuException("Vui lòng nhập đường dẫn hình ảnh");
        if (url.length() > 500) throw new NghiepVuException("Đường dẫn ảnh tối đa 500 ký tự");
        if (!(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/"))) {
            throw new NghiepVuException("Đường dẫn ảnh phải bắt đầu bằng http://, https:// hoặc /");
        }
        String loai = layLoaiTep(url);
        if (loai == null) throw new NghiepVuException("Chỉ hỗ trợ ảnh .jpg, .jpeg, .png, .webp");
        return url;
    }

    /** Tra ve JPG / PNG / WEBP (viet hoa theo CHECK cua DB), hoac null neu duoi file khong hop le. Bo phan ?tham-so phia sau truoc khi xet duoi. */
    private String layLoaiTep(String url) {
        String s = url.toLowerCase();
        int q = s.indexOf('?');
        if (q >= 0) s = s.substring(0, q);
        if (s.endsWith(".jpg") || s.endsWith(".jpeg")) return "JPG";
        if (s.endsWith(".png")) return "PNG";
        if (s.endsWith(".webp")) return "WEBP";
        return null;
    }
}
