package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.HibernateConfig;

/**
 * Chay khi ung dung bat / tat: khoi tao SessionFactory som (loi ket noi DB bao ngay luc khoi dong),
 * bat scheduler khuyen mai (M4), dong SessionFactory khi tat ung dung.
 */
@WebListener
public class KhoiTaoListener implements ServletContextListener {

    private final KhuyenMaiScheduler khuyenMaiScheduler = new KhuyenMaiScheduler();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("PickHub: khoi tao SessionFactory = " + HibernateConfig.getFACTORY());
        // note code nhu nay nhe ban: bat scheduler SAU khi SessionFactory da san sang (scheduler dung DB ngay lan chay dau tien)
        khuyenMaiScheduler.batDau();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // note code nhu nay nhe ban: dung scheduler TRUOC khi dong SessionFactory, tranh scheduler dung DB da dong
        khuyenMaiScheduler.dung();
        HibernateConfig.getFACTORY().close();
    }
}
