package util;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Chay 1 cong viec trong 1 transaction: tu mo session, begin / commit / rollback, dong session.
 * Moi service ghi nhieu bang deu dung lop nay.
 */
public class GiaoDich {

    public interface CongViec<R> {
        R chay(Session session);
    }

    public static <R> R chay(CongViec<R> congViec) {
        Session session = HibernateConfig.getFACTORY().openSession();
        Transaction tx = session.beginTransaction();
        try {
            R ketQua = congViec.chay(session);
            tx.commit();                    // note code nhu nay nhe ban: moi thu OK moi commit
            return ketQua;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback(); // note code nhu nay nhe ban: co loi la huy het, khong ghi do dang
            throw e;
        } finally {
            session.close();
        }
    }
}
