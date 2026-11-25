package com.pickhub.service;

import com.pickhub.dto.KetQuaPhieuDTO;
import com.pickhub.dto.PhieuKhaDungDTO;

import java.math.BigDecimal;
import java.util.List;

/** Kiem tra va tinh tien giam cua phieu giam gia. */
public interface IPhieuGiamGiaService extends IBaseService {

    /**
     * Kiem tra ma giam gia tren tam tinh cua hoa don. Hop le tra ve phieu + so tien giam, khong hop le nem NghiepVuException.
     * LUU Y: kiem tra GIOI HAN SO LAN / SO LUONG SU DUNG dang TAM TAT (xem PhieuGiamGiaServiceImpl).
     */
    KetQuaPhieuDTO tinhGiam(String maGiamGia, BigDecimal tamTinh);

    /**
     * Danh sach voucher dang dung duoc cho popup "Xem va chon voucher" cua POS.
     * Phieu du dieu kien xep truoc (giam nhieu nhat len dau), phieu chua du xep sau (thieu it tien nhat len dau).
     */
    List<PhieuKhaDungDTO> layDanhSachKhaDung(BigDecimal tamTinh);
}
