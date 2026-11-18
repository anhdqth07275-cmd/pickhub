package com.pickhub.service.impl;

import com.pickhub.dto.HoaDonQuayDongDTO;
import com.pickhub.dto.TongKetNgayDTO;
import com.pickhub.dto.TrangHoaDonDTO;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.HoaDonChiTiet;
import com.pickhub.repository.IHoaDonRepository;
import com.pickhub.repository.impl.HoaDonRepository;
import com.pickhub.service.IHoaDonService;
import com.pickhub.util.DinhDangUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonServiceImpl extends BaseServiceImpl implements IHoaDonService {

    private static final int SO_HOA_DON_TOI_DA = 100;

    private final IHoaDonRepository hoaDonRepository = new HoaDonRepository();

    @Override
    public List<HoaDonQuayDongDTO> timHoaDonTaiQuay(String tuKhoa, LocalDate tuNgay, LocalDate denNgay, Long maNhanVien) {
        TrangHoaDonDTO trang = hoaDonRepository.timHoaDonTaiQuay(tuKhoa, tuNgay.atStartOfDay(), denNgay.plusDays(1).atStartOfDay(),
                maNhanVien, 1, SO_HOA_DON_TOI_DA);
        List<HoaDonQuayDongDTO> ketQua = new ArrayList<HoaDonQuayDongDTO>();
        for (HoaDon hoaDon : trang.getDanhSach()) {
            HoaDonQuayDongDTO dong = new HoaDonQuayDongDTO();
            dong.setMaHoaDon(hoaDon.getMaHoaDon());
            dong.setMaTraCuu(hoaDon.getMaTraCuu());
            dong.setThoiGian(DinhDangUtil.ngayGio(hoaDon.getNgayDatHang()));
            dong.setNhanVien(hoaDon.getNhanVienXacNhan() == null ? "" : hoaDon.getNhanVienXacNhan().getHoTen());
            dong.setKhachHang(hoaDon.getTenNguoiNhan());
            dong.setPhuongThuc(nhanPhuongThuc(hoaDon.getPhuongThucThanhToan()));
            dong.setSoMatHang(hoaDonRepository.timDongTheoHoaDon(hoaDon.getMaHoaDon()).size());
            dong.setTongTien(hoaDon.getTongTien());
            dong.setTrangThai(hoaDon.getTrangThai());
            ketQua.add(dong);
        }
        return ketQua;
    }

    /** Chi tra ve hoa don BAN TAI QUAY (khong cho xem hoa don online bang duong dan nay). */
    @Override
    public HoaDon timHoaDonTaiQuayTheoId(Long maHoaDon) {
        HoaDon hoaDon = timHoaDonTheoId(maHoaDon);
        if (hoaDon == null || !"TAI_QUAY".equals(hoaDon.getKenhBan())) {
            return null;
        }
        return hoaDon;
    }

    @Override
    public List<HoaDonChiTiet> layDongHoaDon(Long maHoaDon) {
        return hoaDonRepository.timDongTheoHoaDon(maHoaDon);
    }

    @Override
    public TongKetNgayDTO tongKet(LocalDate tuNgay, LocalDate denNgay, Long maNhanVien) {
        Object[] ketQua = hoaDonRepository.tongKetTaiQuay(tuNgay.atStartOfDay(), denNgay.plusDays(1).atStartOfDay(), maNhanVien);
        long soHoaDon = ketQua[0] == null ? 0 : ((Number) ketQua[0]).longValue();
        BigDecimal doanhThu = ketQua[1] == null ? BigDecimal.ZERO : (BigDecimal) ketQua[1];
        return new TongKetNgayDTO(soHoaDon, doanhThu);
    }

    public static String nhanPhuongThuc(String ma) {
        if ("TIEN_MAT".equals(ma)) {
            return "Tiền mặt";
        }
        if ("CHUYEN_KHOAN".equals(ma)) {
            return "Chuyển khoản";
        }
        if ("THE".equals(ma)) {
            return "Quẹt thẻ";
        }
        return "";
    }
}
