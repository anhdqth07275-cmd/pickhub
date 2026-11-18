package com.pickhub.util;

import com.pickhub.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Tao SessionFactory duy nhat cho ca ung dung. Thong tin ket noi doc tu file src/main/resources/pickhub.properties
 * (doi mat khau o file do, khong phai sua code).
 */
public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = taoSessionFactory();

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    private static SessionFactory taoSessionFactory() {
        Properties cauHinh = new Properties();
        try (InputStream luong = HibernateUtil.class.getClassLoader().getResourceAsStream("pickhub.properties")) {
            if (luong == null) {
                throw new IllegalStateException("Khong tim thay file pickhub.properties trong src/main/resources");
            }
            cauHinh.load(luong);
        } catch (IOException loi) {
            throw new IllegalStateException("Khong doc duoc pickhub.properties", loi);
        }

        Properties hibernate = new Properties();
        hibernate.put(Environment.DIALECT, "org.hibernate.dialect.SQLServer2016Dialect");
        hibernate.put(Environment.DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        hibernate.put(Environment.URL, cauHinh.getProperty("db.url"));
        hibernate.put(Environment.USER, cauHinh.getProperty("db.user"));
        hibernate.put(Environment.PASS, cauHinh.getProperty("db.password"));
        hibernate.put(Environment.SHOW_SQL, cauHinh.getProperty("db.show_sql", "false"));

        Configuration configuration = new Configuration();
        // note code nhu nay nhe ban: khai bao du 18 entity, thieu 1 cai se loi "X is not mapped"
        configuration.addAnnotatedClass(ThuongHieu.class);
        configuration.addAnnotatedClass(DanhMuc.class);
        configuration.addAnnotatedClass(MauSac.class);
        configuration.addAnnotatedClass(ChuViTayCam.class);
        configuration.addAnnotatedClass(KhoiLuong.class);
        configuration.addAnnotatedClass(ChatLieu.class);
        configuration.addAnnotatedClass(NhanVien.class);
        configuration.addAnnotatedClass(KhachHang.class);
        configuration.addAnnotatedClass(SanPham.class);
        configuration.addAnnotatedClass(ChiTietSanPham.class);
        configuration.addAnnotatedClass(HinhAnhSanPham.class);
        configuration.addAnnotatedClass(ChuongTrinhKhuyenMai.class);
        configuration.addAnnotatedClass(ChiTietChuongTrinhKhuyenMai.class);
        configuration.addAnnotatedClass(PhieuGiamGia.class);
        configuration.addAnnotatedClass(HoaDon.class);
        configuration.addAnnotatedClass(HoaDonChiTiet.class);
        configuration.addAnnotatedClass(LichSuTrangThaiHoaDon.class);
        configuration.addAnnotatedClass(SuDungPhieuGiamGia.class);
        configuration.setProperties(hibernate);

        ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(registry);
    }

    /** Chay thu: in ra SessionFactory nghia la ket noi database thanh cong. */
    public static void main(String[] args) {
        System.out.println("Ket noi thanh cong: " + getSessionFactory());
    }
}
