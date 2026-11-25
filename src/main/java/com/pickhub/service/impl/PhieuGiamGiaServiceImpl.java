package com.pickhub.service.impl;

import com.pickhub.dto.KetQuaPhieuDTO;
import com.pickhub.dto.PhieuKhaDungDTO;
import com.pickhub.entity.PhieuGiamGia;
import com.pickhub.repository.IPhieuGiamGiaRepository;
import com.pickhub.repository.impl.PhieuGiamGiaRepository;
import com.pickhub.service.IPhieuGiamGiaService;
import com.pickhub.util.NghiepVuException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PhieuGiamGiaServiceImpl extends BaseServiceImpl implements IPhieuGiamGiaService {

    private final IPhieuGiamGiaRepository phieuGiamGiaRepository = new PhieuGiamGiaRepository();

    @Override
    public KetQuaPhieuDTO tinhGiam(String maGiamGia, BigDecimal tamTinh) {
        if (maGiamGia == null || maGiamGia.trim().isEmpty()) {
            throw new NghiepVuException("Vui lòng nhập mã giảm giá");
        }
        PhieuGiamGia phieu = phieuGiamGiaRepository.timTheoMa(maGiamGia);
        if (phieu == null) {
            throw new NghiepVuException("Mã giảm giá không tồn tại");
        }

        // ---- CAC DIEU KIEN CO BAN (van ap dung): tam dung, thoi gian, gia tri don toi thieu ----
        LocalDateTime bayGio = LocalDateTime.now();
        if ("TAM_DUNG".equals(phieu.getTrangThai())) {
            throw new NghiepVuException("Mã giảm giá đang tạm dừng");
        }
        if (bayGio.isBefore(phieu.getNgayBatDau())) {
            throw new NghiepVuException("Mã giảm giá chưa đến thời gian áp dụng");
        }
        if (bayGio.isAfter(phieu.getNgayKetThuc())) {
            throw new NghiepVuException("Mã giảm giá đã hết hạn");
        }
        if (tamTinh.compareTo(phieu.getGiaTriDonToiThieu()) < 0) {
            throw new NghiepVuException("Hóa đơn cần tối thiểu " + phieu.getGiaTriDonToiThieu().toPlainString() + " đ để dùng mã này");
        }

        // note code nhu nay nhe ban: BAT LAI kiem tra gioi han TONG luot dung (chi DOC soLanDaSuDung nen an toan).
        // Phieu da het luot (do don online dung het) thi POS cung khong cho ap nua. gioiHanSuDung = null = khong gioi han.
        if (phieu.getGioiHanSuDung() != null && phieu.getSoLanDaSuDung() >= phieu.getGioiHanSuDung()) {
            throw new NghiepVuException("Mã giảm giá đã hết lượt sử dụng");
        }

        // =====================================================================================================
        // VAN TAM TAT: gioi han MOI NGUOI (gioiHanMoiNguoi) va viec GHI NHAN luot dung o POS.
        // Ly do: ban tai quay chi co khach le (khong biet khach la ai) nen khong dem duoc "moi nguoi da dung may lan".
        // Gioi han moi nguoi van duoc ep o don ONLINE (PhieuGiamGiaService + SuDungPhieuService).
        // Khi POS co chon khach hang thi bo comment khoi duoi day:
        //
        // if (phieu.getGioiHanMoiNguoi() != null && soLanKhachDaDung >= phieu.getGioiHanMoiNguoi()) {
        //     throw new NghiepVuException("Khách đã dùng hết số lần cho phép của mã này");
        // }
        // =====================================================================================================

        return new KetQuaPhieuDTO(phieu, tinhSoTienGiam(phieu, tamTinh));
    }

    @Override
    public List<PhieuKhaDungDTO> layDanhSachKhaDung(BigDecimal tamTinh) {
        if (tamTinh == null) {
            tamTinh = BigDecimal.ZERO;
        }
        List<PhieuKhaDungDTO> ketQua = new ArrayList<PhieuKhaDungDTO>();
        for (PhieuGiamGia phieu : phieuGiamGiaRepository.layDangKhaDung(LocalDateTime.now())) {
            // note code nhu nay nhe ban: du dieu kien khi tamTinh >= don toi thieu; chua du thi tinh con thieu bao nhieu tien hang
            boolean duDieuKien = tamTinh.compareTo(phieu.getGiaTriDonToiThieu()) >= 0;
            BigDecimal soTienGiam = duDieuKien ? tinhSoTienGiam(phieu, tamTinh) : BigDecimal.ZERO;
            BigDecimal canThem = duDieuKien ? BigDecimal.ZERO : phieu.getGiaTriDonToiThieu().subtract(tamTinh);
            ketQua.add(new PhieuKhaDungDTO(phieu, soTienGiam, duDieuKien, canThem));
        }
        // sap xep: phieu du dieu kien truoc (giam nhieu nhat len dau), phieu chua du sau (thieu it tien nhat len dau)
        Collections.sort(ketQua, new Comparator<PhieuKhaDungDTO>() {
            @Override
            public int compare(PhieuKhaDungDTO a, PhieuKhaDungDTO b) {
                if (a.isDuDieuKien() != b.isDuDieuKien()) {
                    return a.isDuDieuKien() ? -1 : 1;
                }
                if (a.isDuDieuKien()) {
                    return b.getSoTienGiam().compareTo(a.getSoTienGiam());
                }
                return a.getCanThemTien().compareTo(b.getCanThemTien());
            }
        });
        return ketQua;
    }

    /** Giam % (co tran giamToiDa) hoac giam so tien co dinh; khong bao gio giam qua tong tien hang. */
    private BigDecimal tinhSoTienGiam(PhieuGiamGia phieu, BigDecimal tamTinh) {
        BigDecimal giam;
        if ("PHAN_TRAM".equals(phieu.getLoaiGiam())) {
            giam = tamTinh.multiply(phieu.getGiaTriGiam()).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
            if (phieu.getGiamToiDa() != null && giam.compareTo(phieu.getGiamToiDa()) > 0) {
                giam = phieu.getGiamToiDa();
            }
        } else {
            giam = phieu.getGiaTriGiam().setScale(0, RoundingMode.HALF_UP);
        }
        if (giam.compareTo(tamTinh) > 0) {
            giam = tamTinh;
        }
        return giam;
    }
}
