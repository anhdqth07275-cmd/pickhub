package com.pickhub.repository.impl;

import com.pickhub.entity.PhieuGiamGia;
import com.pickhub.repository.IPhieuGiamGiaRepository;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class PhieuGiamGiaRepository extends BaseRepository implements IPhieuGiamGiaRepository {

    @Override
    public PhieuGiamGia timTheoMa(String maGiamGia) {
        Session session = moSession();
        try {
            return session.createQuery("from PhieuGiamGia p where lower(p.maGiamGia) = :ma", PhieuGiamGia.class)
                    .setParameter("ma", maGiamGia.trim().toLowerCase()).uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public List<PhieuGiamGia> layDangKhaDung(LocalDateTime bayGio) {
        Session session = moSession();
        try {
            // note code nhu nay nhe ban: cung dieu kien voi PhieuGiamGiaServiceImpl.tinhGiam (khong tam dung, trong thoi han, con luot tong)
            // gioiHanSuDung = null nghia la KHONG gioi han nen cung duoc lay
            return session.createQuery("from PhieuGiamGia p where p.trangThai <> 'TAM_DUNG' "
                    + "and p.ngayBatDau <= :bayGio and p.ngayKetThuc >= :bayGio "
                    + "and (p.gioiHanSuDung is null or p.soLanDaSuDung < p.gioiHanSuDung) "
                    + "order by p.ngayKetThuc asc, p.maPhieuGiamGia asc", PhieuGiamGia.class)
                    .setParameter("bayGio", bayGio).list();
        } finally {
            session.close();
        }
    }
}
