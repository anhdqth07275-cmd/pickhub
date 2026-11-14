package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.HibernateConfig;

/**
 * Chay khi ung dung bat / tat: khoi tao SessionFactory som (loi ket noi DB bao ngay luc khoi dong),
 * dong SessionFactory khi tat ung dung. Cho scheduler khuyen mai (M4) dang ky them o day.
 */
@WebListener
public class KhoiTaoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("PickHub: khoi tao SessionFactory = " + HibernateConfig.getFACTORY());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateConfig.getFACTORY().close();
    }
}
