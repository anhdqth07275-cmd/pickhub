package exception;

/**
 * Loi nghiep vu: kem thong bao tieng Viet de hien thi cho nguoi dung.
 */
public class NghiepVuException extends RuntimeException {
    public NghiepVuException(String thongBao) {
        super(thongBao);
    }
}
