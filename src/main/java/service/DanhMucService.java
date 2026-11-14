package service;

import dto.DanhMucNodeDTO;
import entity.DanhMuc;
import exception.NghiepVuException;
import repository.DanhMucRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Nghiep vu danh muc (cay cha - con 2 cap: Vot -> Carbon / Fiberglass / Graphite).
 * M3 (san pham) va storefront goi: layCayDanhMuc(), layDanhMucDangHoatDong().
 */
public class DanhMucService {

    private final DanhMucRepository repo = new DanhMucRepository();

    // ---------------- DU LIEU CHO MODULE KHAC ----------------

    // note code nhu nay nhe ban: tra ve cay (goc -> con) chi gom danh muc HOAT_DONG, dung cho menu / bo loc storefront
    public List<DanhMucNodeDTO> layCayDanhMuc() {
        List<DanhMucNodeDTO> cay = new ArrayList<DanhMucNodeDTO>();
        for (DanhMuc goc : repo.layDanhMucGoc()) {
            DanhMucNodeDTO nut = new DanhMucNodeDTO(goc.getMaDanhMuc(), goc.getTenDanhMuc());
            for (DanhMuc con : repo.layDanhMucCon(goc.getMaDanhMuc())) {
                nut.getCon().add(new DanhMucNodeDTO(con.getMaDanhMuc(), con.getTenDanhMuc()));
            }
            cay.add(nut);
        }
        return cay;
    }

    /** Dropdown chon danh muc cho form san pham (M3). */
    public List<DanhMuc> layDanhMucDangHoatDong() {
        return repo.layDangHoatDong();
    }

    /** Dropdown "danh muc cha" trong form: chi cho chon danh muc GOC, bo chinh no ra. */
    public List<DanhMuc> layDanhMucChaCoTheChon(Long idDangSua) {
        List<DanhMuc> ds = new ArrayList<DanhMuc>();
        for (DanhMuc goc : repo.layDanhMucGoc()) {
            if (!goc.getMaDanhMuc().equals(idDangSua)) ds.add(goc);
        }
        return ds;
    }

    public long demSanPham(Long maDanhMuc) {
        return repo.demSanPham(maDanhMuc);
    }

    // ---------------- THEM / SUA ----------------

    /**
     * Them moi (id = null) hoac sua (id != null). Loi nghiep vu thi nem NghiepVuException kem thong bao tieng Viet.
     * Tra ve danh muc da luu.
     */
    public DanhMuc luu(Long id, String ten, Long maCha, String moTa) {
        ten = ten == null ? "" : ten.trim();
        moTa = moTa == null ? "" : moTa.trim();

        // ---- 1. kiem tra ten / mo ta (theo do dai cot trong SQL) ----
        if (ten.isEmpty()) throw new NghiepVuException("Tên danh mục không được để trống");
        if (ten.length() > 100) throw new NghiepVuException("Tên danh mục tối đa 100 ký tự");
        if (moTa.length() > 500) throw new NghiepVuException("Mô tả tối đa 500 ký tự");

        DanhMuc dm = new DanhMuc();
        if (id != null) {
            dm = repo.timTheoId(id);
            if (dm == null) throw new NghiepVuException("Không tìm thấy danh mục");
        }

        // ---- 2. kiem tra danh muc cha ----
        DanhMuc cha = null;
        if (maCha != null) {
            cha = repo.timTheoId(maCha);
            if (cha == null) throw new NghiepVuException("Danh mục cha không tồn tại");
            kiemTraCha(id, cha);
        }

        // ---- 3. khong trung (cha, ten) ----
        if (repo.daTonTai(maCha, ten, id)) {
            throw new NghiepVuException(cha == null ? "Đã có danh mục gốc trùng tên này" : "Danh mục cha \"" + cha.getTenDanhMuc() + "\" đã có danh mục con trùng tên");
        }

        dm.setTenDanhMuc(ten);
        dm.setMoTa(moTa.isEmpty() ? null : moTa);
        dm.setDanhMucCha(cha);
        if (id == null) {
            dm.setTrangThai("HOAT_DONG");
            repo.luu(dm);
        } else {
            repo.capNhat(dm);
        }
        return dm;
    }

    // note code nhu nay nhe ban: cac luat chan khi chon cha. id = danh muc dang sua (null khi them moi)
    private void kiemTraCha(Long id, DanhMuc cha) {
        // luat 1: khong duoc chon chinh no lam cha
        if (id != null && id.equals(cha.getMaDanhMuc())) {
            throw new NghiepVuException("Danh mục không thể là cha của chính nó");
        }
        // luat 2: chan vong lap - di nguoc len theo chuoi cha, gap lai chinh no la vong lap (vd A -> B -> A)
        if (id != null) {
            DanhMuc dangXet = cha;
            while (dangXet != null) {
                if (id.equals(dangXet.getMaDanhMuc())) throw new NghiepVuException("Chọn như vậy sẽ tạo vòng lặp cha – con");
                dangXet = dangXet.getDanhMucCha();
            }
        }
        // luat 3: cay chi 2 cap -> cha phai la danh muc goc (khong co cha)
        if (cha.getDanhMucCha() != null) {
            throw new NghiepVuException("Chỉ được chọn danh mục gốc làm cha (cây chỉ có 2 cấp)");
        }
        // luat 4: danh muc da co con thi khong the tro thanh con cua danh muc khac (se thanh 3 cap)
        if (id != null && repo.demDanhMucCon(id, null) > 0) {
            throw new NghiepVuException("Danh mục này đang có danh mục con nên không thể làm con của danh mục khác");
        }
        // luat 5: khong gan vao cha dang NGUNG
        if (!"HOAT_DONG".equals(cha.getTrangThai())) {
            throw new NghiepVuException("Danh mục cha đang ngừng hoạt động");
        }
    }

    // ---------------- NGUNG / KICH HOAT ----------------

    // note code nhu nay nhe ban: khong xoa cung (SanPham dang tro toi danh muc -> loi khoa ngoai), chi doi HOAT_DONG <-> NGUNG
    public void doiTrangThai(Long id) {
        DanhMuc dm = repo.timTheoId(id);
        if (dm == null) throw new NghiepVuException("Không tìm thấy danh mục");

        if ("HOAT_DONG".equals(dm.getTrangThai())) {
            // ngung cha: phai ngung het con truoc, tranh con "mo coi" van hien tren web
            if (repo.demDanhMucCon(id, "HOAT_DONG") > 0) {
                throw new NghiepVuException("Còn danh mục con đang hoạt động, hãy ngừng danh mục con trước");
            }
            dm.setTrangThai("NGUNG");
        } else {
            // kich hoat lai con: cha cua no cung phai dang hoat dong
            if (dm.getDanhMucCha() != null && !"HOAT_DONG".equals(dm.getDanhMucCha().getTrangThai())) {
                throw new NghiepVuException("Danh mục cha đang ngừng, hãy kích hoạt danh mục cha trước");
            }
            dm.setTrangThai("HOAT_DONG");
        }
        repo.capNhat(dm);
    }
}
