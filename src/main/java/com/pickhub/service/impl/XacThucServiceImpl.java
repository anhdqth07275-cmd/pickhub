package com.pickhub.service.impl;

import com.pickhub.entity.NhanVien;
import com.pickhub.repository.INhanVienRepository;
import com.pickhub.repository.impl.NhanVienRepository;
import com.pickhub.service.IXacThucService;
import com.pickhub.util.MatKhauUtil;
import com.pickhub.util.NghiepVuException;

import java.time.LocalDateTime;

public class XacThucServiceImpl extends BaseServiceImpl implements IXacThucService {

    private static final int SO_LAN_SAI_TOI_DA = 5;

    private final INhanVienRepository nhanVienRepository = new NhanVienRepository();

    @Override
    public NhanVien dangNhap(String thuDienTu, String matKhau) {
        if (thuDienTu == null || thuDienTu.trim().isEmpty() || matKhau == null || matKhau.isEmpty()) {
            throw new NghiepVuException("Vui lòng nhập email và mật khẩu");
        }
        NhanVien nhanVien = nhanVienRepository.timTheoEmail(thuDienTu);
        if (nhanVien == null) {
            throw new NghiepVuException("Sai email hoặc mật khẩu");     // khong tiet lo email nao co ton tai
        }
        if ("KHOA".equals(nhanVien.getTrangThai())) {
            throw new NghiepVuException("Tài khoản đã bị khóa: " + nhanVien.getLyDoKhoa());
        }
        if (!"HOAT_DONG".equals(nhanVien.getTrangThai())) {
            throw new NghiepVuException("Tài khoản chưa được kích hoạt");
        }

        if (!MatKhauUtil.kiemTra(matKhau, nhanVien.getMatKhauMaHoa())) {
            // note code nhu nay nhe ban: sai mat khau thi +1 vao bo dem; du 5 lan thi KHOA tai khoan
            int soLanSai = nhanVien.getSoLanDangNhapSai() + 1;
            nhanVien.setSoLanDangNhapSai(soLanSai);
            String thongBao;
            if (soLanSai >= SO_LAN_SAI_TOI_DA) {
                nhanVien.setTrangThai("KHOA");
                nhanVien.setLyDoKhoa("Nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần");
                nhanVien.setNgayKhoa(LocalDateTime.now());
                thongBao = "Tài khoản đã bị khóa do nhập sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần";
            } else {
                thongBao = "Sai email hoặc mật khẩu (còn " + (SO_LAN_SAI_TOI_DA - soLanSai) + " lần thử)";
            }
            capNhatNhanVien(nhanVien);
            throw new NghiepVuException(thongBao);
        }

        nhanVien.setSoLanDangNhapSai(0);
        nhanVien.setNgayDangNhapCuoi(LocalDateTime.now());
        return capNhatNhanVien(nhanVien);
    }
}
