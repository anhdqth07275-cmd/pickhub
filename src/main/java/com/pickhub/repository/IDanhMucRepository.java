package com.pickhub.repository;

import com.pickhub.entity.DanhMuc;

import java.util.List;

/** Repository chuyen biet cho danh muc. */
public interface IDanhMucRepository extends IBaseRepository {

    /** Danh muc dang HOAT_DONG, sap theo ten. */
    List<DanhMuc> layDangHoatDong();
}
