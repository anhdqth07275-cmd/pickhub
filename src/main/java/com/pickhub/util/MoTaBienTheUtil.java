package com.pickhub.util;

import com.pickhub.entity.ChiTietSanPham;

import java.util.ArrayList;
import java.util.List;

/** Ghep mo ta ngan cua 1 mat hang tu cac thuoc tinh: "Den / 225 g / Carbon / 4.25 inch". */
public class MoTaBienTheUtil {

    private MoTaBienTheUtil() {
    }

    public static String moTa(ChiTietSanPham matHang) {
        List<String> cacPhan = new ArrayList<String>();
        if (matHang.getMauSac() != null) {
            cacPhan.add(matHang.getMauSac().getTenMauSac());
        }
        if (matHang.getKhoiLuong() != null) {
            cacPhan.add(matHang.getKhoiLuong().getKhoiLuongGram() + " g");
        }
        if (matHang.getChatLieu() != null) {
            cacPhan.add(matHang.getChatLieu().getTenChatLieu());
        }
        if (matHang.getChuViTayCam() != null) {
            cacPhan.add(matHang.getChuViTayCam().getChuViInch().stripTrailingZeros().toPlainString() + " inch");
        }
        return String.join(" / ", cacPhan);
    }
}
