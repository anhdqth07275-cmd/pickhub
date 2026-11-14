package service;

import dto.PhanTrang;
import dto.ThuocTinhDTO;
import entity.ChatLieu;
import entity.ChuViTayCam;
import entity.KhoiLuong;
import entity.MauSac;
import exception.NghiepVuException;
import repository.ChatLieuRepository;
import repository.ChuViTayCamRepository;
import repository.KhoiLuongRepository;
import repository.MauSacRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Nghiep vu 4 thuoc tinh san pham: mau sac, chat lieu, khoi luong, chu vi tay cam.
 * "loai" nhan 1 trong: mausac | chat-lieu | khoi-luong | chu-vi (sai thi mac dinh mausac).
 * M3 goi cac ham layXxxDangHoatDong() de do vao dropdown form san pham.
 */
public class ThuocTinhService {

    private final MauSacRepository mauSacRepo = new MauSacRepository();
    private final ChatLieuRepository chatLieuRepo = new ChatLieuRepository();
    private final KhoiLuongRepository khoiLuongRepo = new KhoiLuongRepository();
    private final ChuViTayCamRepository chuViRepo = new ChuViTayCamRepository();

    // ---------------- DROPDOWN CHO M3 (chi lay HOAT_DONG) ----------------
    public List<MauSac> layMauSacDangHoatDong() { return mauSacRepo.layDangHoatDong(); }
    public List<ChatLieu> layChatLieuDangHoatDong() { return chatLieuRepo.layDangHoatDong(); }
    public List<KhoiLuong> layKhoiLuongDangHoatDong() { return khoiLuongRepo.layDangHoatDong(); }
    public List<ChuViTayCam> layChuViDangHoatDong() { return chuViRepo.layDangHoatDong(); }

    // ---------------- TIEN ICH ----------------

    public String chuanHoaLoai(String loai) {
        if ("chat-lieu".equals(loai) || "khoi-luong".equals(loai) || "chu-vi".equals(loai)) return loai;
        return "mausac";
    }

    public String tenThuocTinh(String loai) {
        switch (loai) {
            case "chat-lieu": return "Chất liệu";
            case "khoi-luong": return "Khối lượng";
            case "chu-vi": return "Chu vi tay cầm";
            default: return "Màu sắc";
        }
    }

    // ---------------- DANH SACH: tim kiem + loc trang thai + phan trang ----------------

    // note code nhu nay nhe ban: moi bang chi vai chuc dong nen lay het roi loc + cat trang bang Java cho don gian.
    // (khoi luong / chu vi la SO nen tim bang chuoi o day de dang hon la viet HQL cho tung kieu)
    public PhanTrang<ThuocTinhDTO> timKiem(String loai, String tuKhoa, String trangThai, int trang, int kichThuoc) {
        List<ThuocTinhDTO> khop = new ArrayList<ThuocTinhDTO>();
        String tk = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase();
        for (ThuocTinhDTO t : layTatCa(loai)) {
            boolean dungTuKhoa = tk.isEmpty() || t.getGiaTri().toLowerCase().contains(tk);
            boolean dungTrangThai = trangThai == null || trangThai.isEmpty() || trangThai.equals(t.getTrangThai());
            if (dungTuKhoa && dungTrangThai) khop.add(t);
        }
        int tu = Math.min((trang - 1) * kichThuoc, khop.size());          // cat trang: lay [tu, den)
        int den = Math.min(tu + kichThuoc, khop.size());
        return new PhanTrang<ThuocTinhDTO>(new ArrayList<ThuocTinhDTO>(khop.subList(tu, den)), trang, kichThuoc, khop.size());
    }

    // gom 4 bang khac nhau ve cung 1 kieu ThuocTinhDTO de JSP chi can 1 bang hien thi
    private List<ThuocTinhDTO> layTatCa(String loai) {
        List<ThuocTinhDTO> ds = new ArrayList<ThuocTinhDTO>();
        switch (loai) {
            case "chat-lieu":
                for (ChatLieu c : chatLieuRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(c.getMaChatLieu(), c.getTenChatLieu(), c.getTenChatLieu(), c.getTrangThai(), c.getNgayTao()));
                break;
            case "khoi-luong":
                for (KhoiLuong k : khoiLuongRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(k.getMaKhoiLuong(), k.getKhoiLuongGram() + " g", String.valueOf(k.getKhoiLuongGram()), k.getTrangThai(), k.getNgayTao()));
                break;
            case "chu-vi":
                for (ChuViTayCam c : chuViRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(c.getMaChuViTayCam(), c.getChuViInch().toPlainString() + " inch", c.getChuViInch().toPlainString(), c.getTrangThai(), c.getNgayTao()));
                break;
            default:
                for (MauSac m : mauSacRepo.layTatCa())
                    ds.add(new ThuocTinhDTO(m.getMaMauSac(), m.getTenMauSac(), m.getTenMauSac(), m.getTrangThai(), m.getNgayTao()));
        }
        return ds;
    }

    // ---------------- THEM / SUA / NGUNG ----------------

    public void them(String loai, String giaTri) {
        luu(loai, null, giaTri);
    }

    public void sua(String loai, Long id, String giaTri) {
        if (id == null) throw new NghiepVuException("Thiếu mã thuộc tính");
        luu(loai, id, giaTri);
    }

    // note code nhu nay nhe ban: them va sua dung chung 1 ham, id = null la them moi, id != null la sua
    private void luu(String loai, Long id, String giaTri) {
        giaTri = giaTri == null ? "" : giaTri.trim();
        if (giaTri.isEmpty()) throw new NghiepVuException("Vui lòng nhập giá trị");
        switch (loai) {
            case "chat-lieu": luuChatLieu(id, giaTri); break;
            case "khoi-luong": luuKhoiLuong(id, giaTri); break;
            case "chu-vi": luuChuVi(id, giaTri); break;
            default: luuMauSac(id, giaTri);
        }
    }

    private void luuMauSac(Long id, String ten) {
        if (ten.length() > 50) throw new NghiepVuException("Tên màu tối đa 50 ký tự");
        if (mauSacRepo.daTonTai("tenMauSac", ten, id)) throw new NghiepVuException("Màu sắc đã tồn tại");
        MauSac m = id == null ? new MauSac() : mauSacRepo.timTheoId(id);
        if (m == null) throw new NghiepVuException("Không tìm thấy màu sắc");
        m.setTenMauSac(ten);
        if (id == null) { m.setTrangThai("HOAT_DONG"); mauSacRepo.luu(m); } else mauSacRepo.capNhat(m);
    }

    private void luuChatLieu(Long id, String ten) {
        if (ten.length() > 100) throw new NghiepVuException("Tên chất liệu tối đa 100 ký tự");
        if (chatLieuRepo.daTonTai("tenChatLieu", ten, id)) throw new NghiepVuException("Chất liệu đã tồn tại");
        ChatLieu c = id == null ? new ChatLieu() : chatLieuRepo.timTheoId(id);
        if (c == null) throw new NghiepVuException("Không tìm thấy chất liệu");
        c.setTenChatLieu(ten);
        if (id == null) { c.setTrangThai("HOAT_DONG"); chatLieuRepo.luu(c); } else chatLieuRepo.capNhat(c);
    }

    // note code nhu nay nhe ban: khoi luong la SMALLINT trong DB nen phai > 0 va <= 32767, va khong trung (UNIQUE)
    private void luuKhoiLuong(Long id, String giaTri) {
        int gram;
        try {
            gram = Integer.parseInt(giaTri);
        } catch (NumberFormatException e) {
            throw new NghiepVuException("Khối lượng phải là số nguyên dương (gram)");
        }
        if (gram <= 0 || gram > 32767) throw new NghiepVuException("Khối lượng phải từ 1 đến 32767 gram");
        if (khoiLuongRepo.daTonTai("khoiLuongGram", gram, id)) throw new NghiepVuException("Khối lượng đã tồn tại");
        KhoiLuong k = id == null ? new KhoiLuong() : khoiLuongRepo.timTheoId(id);
        if (k == null) throw new NghiepVuException("Không tìm thấy khối lượng");
        k.setKhoiLuongGram(gram);
        if (id == null) { k.setTrangThai("HOAT_DONG"); khoiLuongRepo.luu(k); } else khoiLuongRepo.capNhat(k);
    }

    // note code nhu nay nhe ban: chu vi la DECIMAL(4,3) -> toi da 9.999 va giu dung 3 so thap phan (4.125)
    private void luuChuVi(Long id, String giaTri) {
        BigDecimal inch;
        try {
            inch = new BigDecimal(giaTri.replace(',', '.')).setScale(3, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException e) {
            throw new NghiepVuException("Chu vi phải là số (inch), ví dụ 4.125");
        }
        if (inch.signum() <= 0 || inch.compareTo(new BigDecimal("10")) >= 0) throw new NghiepVuException("Chu vi phải lớn hơn 0 và nhỏ hơn 10 inch");
        if (chuViRepo.daTonTai("chuViInch", inch, id)) throw new NghiepVuException("Chu vi đã tồn tại");
        ChuViTayCam c = id == null ? new ChuViTayCam() : chuViRepo.timTheoId(id);
        if (c == null) throw new NghiepVuException("Không tìm thấy chu vi");
        c.setChuViInch(inch);
        if (id == null) { c.setTrangThai("HOAT_DONG"); chuViRepo.luu(c); } else chuViRepo.capNhat(c);
    }

    // note code nhu nay nhe ban: "ngung su dung thay vi xoa" - bien the san pham dang dung thuoc tinh nay van giu nguyen, khong loi khoa ngoai
    public void doiTrangThai(String loai, Long id) {
        if (id == null) throw new NghiepVuException("Thiếu mã thuộc tính");
        switch (loai) {
            case "chat-lieu": {
                ChatLieu c = chatLieuRepo.timTheoId(id);
                if (c == null) throw new NghiepVuException("Không tìm thấy chất liệu");
                c.setTrangThai(lat(c.getTrangThai()));
                chatLieuRepo.capNhat(c);
                break;
            }
            case "khoi-luong": {
                KhoiLuong k = khoiLuongRepo.timTheoId(id);
                if (k == null) throw new NghiepVuException("Không tìm thấy khối lượng");
                k.setTrangThai(lat(k.getTrangThai()));
                khoiLuongRepo.capNhat(k);
                break;
            }
            case "chu-vi": {
                ChuViTayCam c = chuViRepo.timTheoId(id);
                if (c == null) throw new NghiepVuException("Không tìm thấy chu vi");
                c.setTrangThai(lat(c.getTrangThai()));
                chuViRepo.capNhat(c);
                break;
            }
            default: {
                MauSac m = mauSacRepo.timTheoId(id);
                if (m == null) throw new NghiepVuException("Không tìm thấy màu sắc");
                m.setTrangThai(lat(m.getTrangThai()));
                mauSacRepo.capNhat(m);
            }
        }
    }

    private String lat(String trangThai) {
        return "HOAT_DONG".equals(trangThai) ? "NGUNG" : "HOAT_DONG";
    }
}
