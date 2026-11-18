package com.pickhub.service.impl;

import com.pickhub.dto.KetQuaPhieuDTO;
import com.pickhub.entity.PhieuGiamGia;
import com.pickhub.repository.IPhieuGiamGiaRepository;
import com.pickhub.repository.impl.PhieuGiamGiaRepository;
import com.pickhub.service.IPhieuGiamGiaService;
import com.pickhub.util.NghiepVuException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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

        // =====================================================================================================
        // TAM TAT (theo yeu cau): kiem tra GIOI HAN SO LAN / SO LUONG SU DUNG phieu giam gia.
        // Khi can bat lai chi viec bo comment khoi duoi day:
        //
        // if (phieu.getGioiHanSuDung() != null && phieu.getSoLanDaSuDung() >= phieu.getGioiHanSuDung()) {
        //     throw new NghiepVuException("Mã giảm giá đã hết lượt sử dụng");
        // }
        // if (phieu.getGioiHanMoiNguoi() != null && soLanKhachDaDung >= phieu.getGioiHanMoiNguoi()) {
        //     throw new NghiepVuException("Khách đã dùng hết số lần cho phép của mã này");
        // }
        //
        // Dong thoi tam khong GHI NHAN luot dung (khong tang soLanDaSuDung, khong them dong SuDungPhieuGiamGia).
        // =====================================================================================================

        return new KetQuaPhieuDTO(phieu, tinhSoTienGiam(phieu, tamTinh));
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
