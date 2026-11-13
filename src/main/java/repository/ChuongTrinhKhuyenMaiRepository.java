package repository;

import entity.ChuongTrinhKhuyenMai;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateConfig;
import java.util.List;


public class ChuongTrinhKhuyenMaiRepository {

    public List<ChuongTrinhKhuyenMai> getAll() {
        try (Session session = HibernateConfig.getFACTORY().openSession()) {
            return session.createQuery("FROM ChuongTrinhKhuyenMai ORDER BY id DESC", ChuongTrinhKhuyenMai.class).list();
        }
    }

    public ChuongTrinhKhuyenMai findById(Integer id) {
        try (Session session = HibernateConfig.getFACTORY().openSession()) {
            return session.get(ChuongTrinhKhuyenMai.class, id);
        }
    }

    public boolean saveOrUpdate(ChuongTrinhKhuyenMai ctkm) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getFACTORY().openSession()) {
            tx = session.beginTransaction();
            session.merge(ctkm);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Integer id) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getFACTORY().openSession()) {
            tx = session.beginTransaction();
            ChuongTrinhKhuyenMai item = session.get(ChuongTrinhKhuyenMai.class, id);
            if (item != null) session.remove(item);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}