package com.pickhub.dto;

import java.util.List;

/** Ket qua 1 lan tim san pham cho man hinh ban hang: danh sach mat hang + thong tin phan trang. */
public class TrangBienTheBanDTO {

    private final List<BienTheBanDTO> danhSach;
    private final long tongSo;
    private final int trang;
    private final int kichThuoc;

    public TrangBienTheBanDTO(List<BienTheBanDTO> danhSach, long tongSo, int trang, int kichThuoc) {
        this.danhSach = danhSach;
        this.tongSo = tongSo;
        this.trang = trang;
        this.kichThuoc = kichThuoc;
    }

    public List<BienTheBanDTO> getDanhSach() {
        return danhSach;
    }

    public long getTongSo() {
        return tongSo;
    }

    public int getTrang() {
        return trang;
    }

    public int getTongTrang() {
        return (int) Math.max(1, (tongSo + kichThuoc - 1) / kichThuoc);
    }

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
