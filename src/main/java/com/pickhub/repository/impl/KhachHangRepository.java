package com.pickhub.repository.impl;

import com.pickhub.entity.KhachHang;
import com.pickhub.repository.IKhachHangRepository;
import org.hibernate.Session;

public class KhachHangRepository extends BaseRepository implements IKhachHangRepository {

    @Override
    public KhachHang timTheoEmail(String thuDienTu) {
        Session session = moSession();
        try {
            return session.createQuery("from KhachHang k where lower(k.thuDienTu) = :email", KhachHang.class)
                    .setParameter("email", thuDienTu.trim().toLowerCase()).uniqueResult();
        } finally {
            session.close();
        }
    }
}
