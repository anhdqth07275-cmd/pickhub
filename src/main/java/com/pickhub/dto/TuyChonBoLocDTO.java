package com.pickhub.dto;

import com.pickhub.entity.ChatLieu;
import com.pickhub.entity.ChuViTayCam;
import com.pickhub.entity.DanhMuc;
import com.pickhub.entity.KhoiLuong;
import com.pickhub.entity.MauSac;
import com.pickhub.entity.ThuongHieu;

import java.util.ArrayList;
import java.util.List;

/** Cac lua chon hien trong thanh bo loc (danh muc, thuong hieu, mau, khoi luong, chat lieu, chu vi). */
public class TuyChonBoLocDTO {

    private final List<DanhMuc> danhMucGoc = new ArrayList<DanhMuc>();
    private final List<DanhMuc> danhMucCon = new ArrayList<DanhMuc>();
    private final List<ThuongHieu> thuongHieu = new ArrayList<ThuongHieu>();
    private final List<MauSac> mauSac = new ArrayList<MauSac>();
    private final List<KhoiLuong> khoiLuong = new ArrayList<KhoiLuong>();
    private final List<ChatLieu> chatLieu = new ArrayList<ChatLieu>();
    private final List<ChuViTayCam> chuVi = new ArrayList<ChuViTayCam>();

    public List<DanhMuc> getDanhMucGoc() {
        return danhMucGoc;
    }

    public List<DanhMuc> getDanhMucCon() {
        return danhMucCon;
    }

    public List<ThuongHieu> getThuongHieu() {
        return thuongHieu;
    }

    public List<MauSac> getMauSac() {
        return mauSac;
    }

    public List<KhoiLuong> getKhoiLuong() {
        return khoiLuong;
    }

    public List<ChatLieu> getChatLieu() {
        return chatLieu;
    }

    public List<ChuViTayCam> getChuVi() {
        return chuVi;
    }
}
