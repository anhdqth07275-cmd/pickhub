package com.pickhub.service;

import com.pickhub.dto.KetQuaGiaDTO;
import com.pickhub.entity.ChiTietSanPham;

import java.util.List;
import java.util.Map;

/** Tinh gia ban sau khuyen mai theo san pham. */
public interface IKhuyenMaiService extends IBaseService {

    /** Gia ban cua nhieu mat hang cung luc (1 lan truy van): maChiTietSanPham -> KetQuaGiaDTO. */
    Map<Long, KetQuaGiaDTO> tinhGiaNhieuMatHang(List<ChiTietSanPham> danhSach);

    KetQuaGiaDTO tinhGia(ChiTietSanPham matHang);
}
