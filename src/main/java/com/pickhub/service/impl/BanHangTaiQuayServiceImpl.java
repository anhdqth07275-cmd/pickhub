package com.pickhub.service.impl;

import com.pickhub.dto.DongBanDTO;
import com.pickhub.dto.KetQuaGiaDTO;
import com.pickhub.dto.KetQuaPhieuDTO;
import com.pickhub.entity.ChiTietSanPham;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.HoaDonChiTiet;
import com.pickhub.entity.KhachHang;
import com.pickhub.repository.IChiTietSanPhamRepository;
import com.pickhub.repository.IHoaDonRepository;
import com.pickhub.repository.IKhachHangRepository;
import com.pickhub.repository.impl.ChiTietSanPhamRepository;
import com.pickhub.repository.impl.HoaDonRepository;
import com.pickhub.repository.impl.KhachHangRepository;
import com.pickhub.service.IBanHangTaiQuayService;
import com.pickhub.service.IKhuyenMaiService;
import com.pickhub.service.IPhieuGiamGiaService;
import com.pickhub.util.MoTaBienTheUtil;
import com.pickhub.util.NghiepVuException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BanHangTaiQuayServiceImpl extends BaseServiceImpl implements IBanHangTaiQuayService {

    /** Email cua tai khoan "Khach le tai quay" (do sql/02_Migration_POS.sql tao). */
    public static final String EMAIL_KHACH_LE = "khachle@pickhub.local";

    private static final int SO_LUONG_TOI_DA_MOI_DONG = 999;
    private static final SecureRandom NGAU_NHIEN = new SecureRandom();

    private final IChiTietSanPhamRepository chiTietSanPhamRepository = new ChiTietSanPhamRepository();
    private final IKhachHangRepository khachHangRepository = new KhachHangRepository();
    private final IHoaDonRepository hoaDonRepository = new HoaDonRepository();
    private final IKhuyenMaiService khuyenMaiService = new KhuyenMaiServiceImpl();
    private final IPhieuGiamGiaService phieuGiamGiaService = new PhieuGiamGiaServiceImpl();

    @Override
    public HoaDon thanhToan(Long maNhanVien, List<DongBanDTO> danhSachDong, String maGiamGia, String phuongThuc,
                            BigDecimal tienKhachDua, String tenKhach, String soDienThoaiKhach, String ghiChu) {

        // ---- 1. kiem tra dau vao ----
        if (maNhanVien == null) {
            throw new NghiepVuException("Chưa đăng nhập");
        }
        if (danhSachDong == null || danhSachDong.isEmpty()) {
            throw new NghiepVuException("Hóa đơn chưa có mặt hàng nào");
        }
        if (!"TIEN_MAT".equals(phuongThuc) && !"CHUYEN_KHOAN".equals(phuongThuc) && !"THE".equals(phuongThuc)) {
            throw new NghiepVuException("Vui lòng chọn hình thức thanh toán");
        }
        String ten = tenKhach == null ? "" : tenKhach.trim();
        String sdt = soDienThoaiKhach == null ? "" : soDienThoaiKhach.trim();
        String ghiChuSach = ghiChu == null ? "" : ghiChu.trim();
        if (ten.length() > 100) {
            throw new NghiepVuException("Tên khách tối đa 100 ký tự");
        }
        if (!sdt.isEmpty() && !sdt.matches("^0\\d{9,10}$")) {
            throw new NghiepVuException("Số điện thoại khách phải bắt đầu bằng 0 và gồm 10 - 11 chữ số");
        }
        if (ghiChuSach.length() > 500) {
            throw new NghiepVuException("Ghi chú tối đa 500 ký tự");
        }

        // ---- 2. gop cac dong trung mat hang (CSDL khong cho 1 mat hang xuat hien 2 lan trong 1 hoa don) ----
        Map<Long, Integer> soLuongTheoMatHang = new LinkedHashMap<Long, Integer>();
        for (DongBanDTO dong : danhSachDong) {
            if (dong.getMaChiTietSanPham() == null || dong.getSoLuong() <= 0) {
                throw new NghiepVuException("Số lượng mỗi mặt hàng phải lớn hơn 0");
            }
            Integer daCo = soLuongTheoMatHang.get(dong.getMaChiTietSanPham());
            int tong = (daCo == null ? 0 : daCo) + dong.getSoLuong();
            if (tong > SO_LUONG_TOI_DA_MOI_DONG) {
                throw new NghiepVuException("Mỗi mặt hàng tối đa " + SO_LUONG_TOI_DA_MOI_DONG + " cái trên một hóa đơn");
            }
            soLuongTheoMatHang.put(dong.getMaChiTietSanPham(), tong);
        }

        // ---- 3. nap mat hang that tu database, kiem tra con ban / du ton, tinh gia ----
        List<Long> danhSachMa = new ArrayList<Long>(soLuongTheoMatHang.keySet());
        List<ChiTietSanPham> matHangThat = chiTietSanPhamRepository.timTheoDanhSachId(danhSachMa);
        if (matHangThat.size() != danhSachMa.size()) {
            throw new NghiepVuException("Có mặt hàng không còn tồn tại trong hệ thống, vui lòng tải lại trang");
        }
        Map<Long, KetQuaGiaDTO> bangGia = khuyenMaiService.tinhGiaNhieuMatHang(matHangThat);

        List<HoaDonChiTiet> dongHoaDon = new ArrayList<HoaDonChiTiet>();
        BigDecimal tamTinh = BigDecimal.ZERO;
        for (ChiTietSanPham matHang : matHangThat) {
            int soLuong = soLuongTheoMatHang.get(matHang.getMaChiTietSanPham());
            String tenMatHang = matHang.getSanPham().getTenSanPham();
            if (!"DANG_BAN".equals(matHang.getTrangThai())) {
                throw new NghiepVuException("\"" + tenMatHang + "\" (" + matHang.getMaHang() + ") hiện không bán");
            }
            if (matHang.getSoLuongTon() < soLuong) {
                throw new NghiepVuException("\"" + tenMatHang + "\" (" + matHang.getMaHang() + ") chỉ còn " + matHang.getSoLuongTon() + " cái");
            }
            KetQuaGiaDTO gia = bangGia.get(matHang.getMaChiTietSanPham());

            HoaDonChiTiet dong = new HoaDonChiTiet();
            dong.setMaChiTietSanPham(matHang.getMaChiTietSanPham());
            dong.setMaChiTietChuongTrinhKhuyenMai(gia.getMaChiTietChuongTrinhKhuyenMai());
            // note code nhu nay nhe ban: CHOT ten, ma hang, mo ta, gia vao dong hoa don - sau nay doi gia / doi ten san pham thi hoa don cu van dung
            dong.setTenSanPham(tenMatHang);
            dong.setMaHang(matHang.getMaHang());
            String moTa = MoTaBienTheUtil.moTa(matHang);
            dong.setMoTaChiTiet(moTa.length() > 300 ? moTa.substring(0, 300) : moTa);
            dong.setDonGiaGoc(gia.getGiaGoc());
            dong.setDonGia(gia.getGiaBan());
            dong.setSoLuong(soLuong);
            dongHoaDon.add(dong);
            tamTinh = tamTinh.add(gia.getGiaBan().multiply(BigDecimal.valueOf(soLuong)));
        }

        // ---- 4. ma giam gia (neu co) ----
        BigDecimal soTienGiam = BigDecimal.ZERO;
        HoaDon hoaDon = new HoaDon();
        if (maGiamGia != null && !maGiamGia.trim().isEmpty()) {
            KetQuaPhieuDTO ketQuaPhieu = phieuGiamGiaService.tinhGiam(maGiamGia, tamTinh);
            soTienGiam = ketQuaPhieu.getSoTienGiam();
            hoaDon.setPhieuGiamGia(ketQuaPhieu.getPhieu());
        }
        BigDecimal tongTien = tamTinh.subtract(soTienGiam);

        // ---- 5. tien khach dua / tien thua ----
        BigDecimal tienThua = BigDecimal.ZERO;
        BigDecimal khachDua = tienKhachDua;
        if ("TIEN_MAT".equals(phuongThuc)) {
            if (khachDua == null || khachDua.compareTo(tongTien) < 0) {
                throw new NghiepVuException("Tiền khách đưa chưa đủ (cần tối thiểu " + tongTien.toPlainString() + " đ)");
            }
            tienThua = khachDua.subtract(tongTien);
        } else {
            khachDua = tongTien;       // chuyen khoan / quet the: tra dung so tien, khong co tien thua
        }

        // ---- 6. dung hoa don (khach le tai quay) ----
        KhachHang khachLe = khachHangRepository.timTheoEmail(EMAIL_KHACH_LE);
        if (khachLe == null) {
            throw new NghiepVuException("Chưa có tài khoản \"Khách lẻ\" - hãy chạy file sql/02_Migration_POS.sql");
        }
        LocalDateTime bayGio = LocalDateTime.now();
        hoaDon.setKhachHang(khachLe);
        hoaDon.setMaTraCuu(taoMaHoaDon(bayGio));
        hoaDon.setTenNguoiNhan(ten.isEmpty() ? "Khách lẻ" : ten);
        hoaDon.setSoDienThoaiNguoiNhan(sdt.isEmpty() ? "0000000000" : sdt);
        hoaDon.setTinhThanhGiao("Tại quầy");              // cac cot dia chi giao hang bat buoc co du lieu (CSDL), ban tai quay ghi "Tai quay"
        hoaDon.setQuanHuyenGiao("Tại quầy");
        hoaDon.setPhuongXaGiao("Tại quầy");
        hoaDon.setSoNhaDuongGiao("Mua trực tiếp tại quầy");
        hoaDon.setTamTinh(tamTinh);
        hoaDon.setSoTienGiam(soTienGiam);
        hoaDon.setTongTien(tongTien);
        hoaDon.setTrangThai("HOAN_THANH");                 // ban tai quay: thanh toan va giao hang ngay
        hoaDon.setGhiChu(ghiChuSach.isEmpty() ? null : ghiChuSach);
        hoaDon.setNgayDatHang(bayGio);
        hoaDon.setNgayXacNhan(bayGio);
        hoaDon.setNgayGiaoThanhCong(bayGio);
        hoaDon.setKenhBan("TAI_QUAY");
        hoaDon.setPhuongThucThanhToan(phuongThuc);
        hoaDon.setTienKhachDua(khachDua);
        hoaDon.setTienThua(tienThua);

        // ---- 7. luu trong 1 giao dich: tru ton + hoa don + dong + lich su ----
        return hoaDonRepository.luuHoaDonTaiQuay(hoaDon, dongHoaDon, maNhanVien);
    }

    /** Ma hoa don dang HDyyMMddHHmmss + 2 so ngau nhien (toi da 20 ky tu theo cot maTraCuu). */
    private String taoMaHoaDon(LocalDateTime bayGio) {
        return "HD" + bayGio.format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + String.format("%02d", NGAU_NHIEN.nextInt(100));
    }
}
