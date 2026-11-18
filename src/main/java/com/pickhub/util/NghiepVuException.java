package com.pickhub.util;

/**
 * Loi NGHIEP VU (nguoi dung nhap sai, het hang, ma giam gia khong hop le...).
 * Thong bao viet tieng Viet de Servlet tra thang cho man hinh.
 */
public class NghiepVuException extends RuntimeException {

    public NghiepVuException(String thongBao) {
        super(thongBao);
    }
}
