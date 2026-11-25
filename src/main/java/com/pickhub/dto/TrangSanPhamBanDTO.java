package com.pickhub.dto;

import java.util.List;

/** Ket qua 1 lan tim san pham THEO SAN PHAM cho man hinh ban hang: danh sach the san pham + thong tin phan trang. */
public class TrangSanPhamBanDTO {

    private final List<SanPhamBanDTO> danhSach;
    private final long tongSo;          // tong so SAN PHAM (khong phai so bien the)
    private final int trang;
    private final int kichThuoc;

    public TrangSanPhamBanDTO(List<SanPhamBanDTO> danhSach, long tongSo, int trang, int kichThuoc) {
        this.danhSach = danhSach;
        this.tongSo = tongSo;
        this.trang = trang;
        this.kichThuoc = kichThuoc;
    }

    public List<SanPhamBanDTO> getDanhSach() {
        return danhSach;
    }

    public long getTongSo() {
        return tongSo;
    }

    public int getTongTrang() {
        return (int) Math.max(1, (tongSo + kichThuoc - 1) / kichThuoc);
    }

    /** Cung khuon voi TrangBienTheBanDTO.toJson() nen phan tai trang / phan trang trong pos.js dung lai duoc. */
    public String toJson() {
        StringBuilder sb = new StringBuilder("{\"ok\":true,\"tongSo\":").append(tongSo)
                .append(",\"trang\":").append(trang).append(",\"tongTrang\":").append(getTongTrang()).append(",\"ds\":[");
        for (int i = 0; i < danhSach.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(danhSach.get(i).toJson());
        }
        return sb.append("]}").toString();
    }
}
