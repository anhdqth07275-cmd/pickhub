package listener;

import service.KhuyenMaiService;
import service.PhieuGiamGiaService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Tu dong doi trang thai theo thoi gian, chay moi phut (module M4):
 *  - Chuong trinh khuyen mai: SAP_CHAY -> DANG_CHAY -> KET_THUC
 *  - Phieu giam gia         : SAP_CHAY -> DANG_CHAY -> HET_HAN
 * Duoc bat / tat trong KhoiTaoListener (khi ung dung khoi dong / dung).
 */
public class KhuyenMaiScheduler {

    private ScheduledExecutorService boLenhLich;
    private final KhuyenMaiService khuyenMaiService = new KhuyenMaiService();
    private final PhieuGiamGiaService phieuService = new PhieuGiamGiaService();

    public void batDau() {
        // note code nhu nay nhe ban: thread daemon de Tomcat tat ung dung la thread nay tu dung, khong bi treo
        boLenhLich = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "khuyen-mai-scheduler");
            t.setDaemon(true);
            return t;
        });
        // chay ngay lan dau luc khoi dong, sau do lap lai moi 1 phut
        boLenhLich.scheduleAtFixedRate(this::chayMotLan, 0, 1, TimeUnit.MINUTES);
    }

    public void dung() {
        if (boLenhLich != null) boLenhLich.shutdownNow();
    }

    private void chayMotLan() {
        // note code nhu nay nhe ban: PHAI bat het loi o day - neu de loi thoat ra ngoai thi ScheduledExecutor se HUY cac lan chay sau
        try {
            int soCT = khuyenMaiService.capNhatTrangThaiTheoThoiGian();
            int soPhieu = phieuService.capNhatTrangThaiTheoThoiGian();
            if (soCT > 0 || soPhieu > 0) {
                System.out.println("PickHub scheduler: doi trang thai " + soCT + " chuong trinh khuyen mai, " + soPhieu + " phieu giam gia");
            }
        } catch (Exception e) {
            System.err.println("PickHub scheduler loi: " + e.getMessage());
        }
    }
}
