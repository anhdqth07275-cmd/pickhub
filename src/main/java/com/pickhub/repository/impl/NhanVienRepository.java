package com.pickhub.repository.impl;

import com.pickhub.entity.NhanVien;
import com.pickhub.repository.INhanVienRepository;
import org.hibernate.Session;

public class NhanVienRepository extends BaseRepository implements INhanVienRepository {

    @Override
    public NhanVien timTheoEmail(String thuDienTu) {
        Session session = moSession();
        try {
            return session.createQuery("from NhanVien n where lower(n.thuDienTu) = :email", NhanVien.class)
                    .setParameter("email", thuDienTu.trim().toLowerCase()).uniqueResult();
        } finally {
            session.close();
        }
    }
}
