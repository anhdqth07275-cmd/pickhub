package com.pickhub.service;

import com.pickhub.entity.NhanVien;

/** Dang nhap cho nhan vien ban hang tai quay. */
public interface IXacThucService extends IBaseService {

    /** Dang nhap bang email + mat khau. Sai thong tin / bi khoa thi nem NghiepVuException kem thong bao. */
    NhanVien dangNhap(String thuDienTu, String matKhau);
}
