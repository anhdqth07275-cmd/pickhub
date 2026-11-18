package com.pickhub.repository.impl;

import com.pickhub.entity.DanhMuc;
import com.pickhub.repository.IDanhMucRepository;
import org.hibernate.Session;

import java.util.List;

public class DanhMucRepository extends BaseRepository implements IDanhMucRepository {

    @Override
    public List<DanhMuc> layDangHoatDong() {
        Session session = moSession();
        try {
            return session.createQuery("from DanhMuc d where d.trangThai = 'HOAT_DONG' order by d.tenDanhMuc", DanhMuc.class).list();
        } finally {
            session.close();
        }
    }
}
