package service;

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
import util.DocSoUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 4 thuoc tinh san pham (M2): mau sac, chat lieu, khoi luong, chu vi tay cam.
 * Dung chung 1 service, phan biet bang tham so "loai": "mau-sac" / "chat-lieu" / "khoi-luong" / "chu-vi".
 * M3 goi layDangHoatDong(loai) cho dropdown form bien the.
 */
public class ThuocTinhService {

    public static final String MAU_SAC = "mau-sac";
    public static final String CHAT_LIEU = "chat-lieu";
    public static final String KHOI_LUONG = "khoi-luong";
    public static final String CHU_VI = "chu-vi";

    private final MauSacRepository mauRepo = new MauSacRepository();
    private final ChatLieuRepository chatLieuRepo = new ChatLieuRepository();
    private final KhoiLuongRepository khoiLuongRepo = new KhoiLuongRepository();
    private final ChuViTayCamRepository chuViRepo = new ChuViTayCamRepository();

    /** Tat ca gia tri cua 1 loai thuoc tinh. */
    public List<ThuocTinhDTO> layDanhSach(String loai) {
        List<ThuocTinhDTO> kq = new ArrayList<ThuocTinhDTO>();
        switch (loai) {
            case MAU_SAC:
                for (MauSac x : mauRepo.layTatCa()) kq.add(new ThuocTinhDTO(x.getMaMauSac(), x.getTenMauSac(), x.getTenMauSac(), x.getTrangThai(), x.getNgayTao()));
                break;
            case CHAT_LIEU:
                for (ChatLieu x : chatLieuRepo.layTatCa()) kq.add(new ThuocTinhDTO(x.getMaChatLieu(), x.getTenChatLieu(), x.getTenChatLieu(), x.getTrangThai(), x.getNgayTao()));
                break;
            case KHOI_LUONG:
                for (KhoiLuong x : khoiLuongRepo.layTatCa()) kq.add(new ThuocTinhDTO(x.getMaKhoiLuong(), x.getKhoiLuongGram() + " g", String.valueOf(x.getKhoiLuongGram()), x.getTrangThai(), x.getNgayTao()));
                break;
            case CHU_VI:
                for (ChuViTayCam x : chuViRepo.layTatCa()) {
                    String so = x.getChuViInch().stripTrailingZeros().toPlainString();
                    kq.add(new ThuocTinhDTO(x.getMaChuViTayCam(), so + " inch", so, x.getTrangThai(), x.getNgayTao()));
                }
                break;
            default:
                throw new NghiepVuException("Loại thuộc tính không hợp lệ");
        }
        return kq;
    }

    /** Chi cac gia tri HOAT_DONG - dung cho dropdown form bien the san pham (M3). */
    public List<ThuocTinhDTO> layDangHoatDong(String loai) {
        List<ThuocTinhDTO> kq = new ArrayList<ThuocTinhDTO>();
        for (ThuocTinhDTO t : layDanhSach(loai)) {
            if ("HOAT_DONG".equals(t.getTrangThai())) kq.add(t);
        }
        return kq;
    }

    /** Them (id = null) hoac sua (id != null) 1 gia tri thuoc tinh. */
    public void luu(String loai, Long id, String giaTri) {
        giaTri = giaTri == null ? "" : giaTri.trim();
        if (giaTri.isEmpty()) throw new NghiepVuException("Giá trị không được để trống");
        switch (loai) {
            case MAU_SAC: {
                if (giaTri.length() > 50) throw new NghiepVuException("Tên màu tối đa 50 ký tự");
                if (mauRepo.daTonTai("tenMauSac", giaTri, id)) throw new NghiepVuException("Màu này đã tồn tại");
                MauSac x = id == null ? new MauSac() : mauRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy màu sắc");
                x.setTenMauSac(giaTri);
                if (id == null) { x.setTrangThai("HOAT_DONG"); mauRepo.luu(x); } else mauRepo.capNhat(x);
                break;
            }
            case CHAT_LIEU: {
                if (giaTri.length() > 100) throw new NghiepVuException("Tên chất liệu tối đa 100 ký tự");
                if (chatLieuRepo.daTonTai("tenChatLieu", giaTri, id)) throw new NghiepVuException("Chất liệu này đã tồn tại");
                ChatLieu x = id == null ? new ChatLieu() : chatLieuRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy chất liệu");
                x.setTenChatLieu(giaTri);
                if (id == null) { x.setTrangThai("HOAT_DONG"); chatLieuRepo.luu(x); } else chatLieuRepo.capNhat(x);
                break;
            }
            case KHOI_LUONG: {
                Integer gram = DocSoUtil.soNguyen(giaTri, "Khối lượng");
                // note code nhu nay nhe ban: vot pickleball nang khoang 200 - 270g, cho phep 1 - 2000g cho thoang
                if (gram == null || gram < 1 || gram > 2000) throw new NghiepVuException("Khối lượng phải từ 1 đến 2000 gram");
                if (khoiLuongRepo.daTonTai("khoiLuongGram", gram, id)) throw new NghiepVuException("Khối lượng này đã tồn tại");
                KhoiLuong x = id == null ? new KhoiLuong() : khoiLuongRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy khối lượng");
                x.setKhoiLuongGram(gram);
                if (id == null) { x.setTrangThai("HOAT_DONG"); khoiLuongRepo.luu(x); } else khoiLuongRepo.capNhat(x);
                break;
            }
            case CHU_VI: {
                BigDecimal inch = DocSoUtil.soThapPhan(giaTri, "Chu vi tay cầm");
                if (inch == null || inch.signum() <= 0 || inch.compareTo(new BigDecimal("9.999")) > 0) throw new NghiepVuException("Chu vi phải lớn hơn 0 và nhỏ hơn 10 inch");
                if (chuViRepo.daTonTai("chuViInch", inch, id)) throw new NghiepVuException("Chu vi này đã tồn tại");
                ChuViTayCam x = id == null ? new ChuViTayCam() : chuViRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy chu vi tay cầm");
                x.setChuViInch(inch);
                if (id == null) { x.setTrangThai("HOAT_DONG"); chuViRepo.luu(x); } else chuViRepo.capNhat(x);
                break;
            }
            default:
                throw new NghiepVuException("Loại thuộc tính không hợp lệ");
        }
    }

    // note code nhu nay nhe ban: khong xoa cung vi bien the san pham (M3) dang tro toi; chi NGUNG de an khoi dropdown
    public void doiTrangThai(String loai, Long id) {
        switch (loai) {
            case MAU_SAC: {
                MauSac x = mauRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy màu sắc");
                x.setTrangThai(daoTrangThai(x.getTrangThai()));
                mauRepo.capNhat(x);
                break;
            }
            case CHAT_LIEU: {
                ChatLieu x = chatLieuRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy chất liệu");
                x.setTrangThai(daoTrangThai(x.getTrangThai()));
                chatLieuRepo.capNhat(x);
                break;
            }
            case KHOI_LUONG: {
                KhoiLuong x = khoiLuongRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy khối lượng");
                x.setTrangThai(daoTrangThai(x.getTrangThai()));
                khoiLuongRepo.capNhat(x);
                break;
            }
            case CHU_VI: {
                ChuViTayCam x = chuViRepo.timTheoId(id);
                if (x == null) throw new NghiepVuException("Không tìm thấy chu vi tay cầm");
                x.setTrangThai(daoTrangThai(x.getTrangThai()));
                chuViRepo.capNhat(x);
                break;
            }
            default:
                throw new NghiepVuException("Loại thuộc tính không hợp lệ");
        }
    }

    private String daoTrangThai(String hienTai) {
        return "HOAT_DONG".equals(hienTai) ? "NGUNG" : "HOAT_DONG";
    }
}
