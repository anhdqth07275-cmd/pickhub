package com.pickhub.listener;

import com.pickhub.util.HibernateUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Khoi tao ket noi database ngay khi Tomcat chay (loi cau hinh bao ngay luc khoi dong) va dong lai khi tat. */
@WebListener
public class KhoiTaoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent su) {
        System.out.println("PickHub POS: da ket noi database - " + HibernateUtil.getSessionFactory());
    }

    @Override
    public void contextDestroyed(ServletContextEvent su) {
        HibernateUtil.getSessionFactory().close();
    }
}
