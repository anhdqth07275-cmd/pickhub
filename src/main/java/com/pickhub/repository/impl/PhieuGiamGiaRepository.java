package com.pickhub.repository.impl;

import com.pickhub.entity.PhieuGiamGia;
import com.pickhub.repository.IPhieuGiamGiaRepository;
import org.hibernate.Session;

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
}
