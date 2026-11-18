package com.pickhub.service;

import com.pickhub.dto.BienTheBanDTO;
import com.pickhub.dto.BoLocBienTheDTO;
import com.pickhub.dto.TrangBienTheBanDTO;
import com.pickhub.dto.TuyChonBoLocDTO;

/** Tim kiem / loc san pham cho man hinh ban hang. */
public interface ISanPhamService extends IBaseService {

    /** Tim + loc + sap xep + phan trang, tra ve mat hang da kem anh va gia sau khuyen mai. */
    TrangBienTheBanDTO timKiemBienThe(BoLocBienTheDTO boLoc);

    /** Tim 1 mat hang theo MA HANG chinh xac (quet ma vach). Khong co / het hang thi null. */
    BienTheBanDTO timTheoMaHang(String maHang);

    /** Cac lua chon cho thanh bo loc (danh muc, thuong hieu, mau, khoi luong, chat lieu, chu vi). */
    TuyChonBoLocDTO layTuyChonBoLoc();
}
