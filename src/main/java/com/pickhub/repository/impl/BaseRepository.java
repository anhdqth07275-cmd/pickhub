package com.pickhub.repository.impl;

import com.pickhub.entity.ThuongHieu;
import com.pickhub.entity.DanhMuc;
import com.pickhub.entity.MauSac;
import com.pickhub.entity.ChuViTayCam;
import com.pickhub.entity.KhoiLuong;
import com.pickhub.entity.ChatLieu;
import com.pickhub.entity.NhanVien;
import com.pickhub.entity.KhachHang;
import com.pickhub.entity.SanPham;
import com.pickhub.entity.ChiTietSanPham;
import com.pickhub.entity.HinhAnhSanPham;
import com.pickhub.entity.ChuongTrinhKhuyenMai;
import com.pickhub.entity.ChiTietChuongTrinhKhuyenMai;
import com.pickhub.entity.PhieuGiamGia;
import com.pickhub.entity.HoaDon;
import com.pickhub.entity.HoaDonChiTiet;
import com.pickhub.entity.LichSuTrangThaiHoaDon;
import com.pickhub.entity.SuDungPhieuGiamGia;
import com.pickhub.repository.IBaseRepository;
import com.pickhub.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * BASE REPOSITORY: noi TAP TRUNG toan bo ham CRUD cho moi entity (viet tuong minh, KHONG generic).
 * Cac repository chuyen biet (SanPhamRepository, HoaDonRepository...) ke thua lop nay de dung lai CRUD va them truy van rieng.
 *
 * note code nhu nay nhe ban: moi ham tu mo Session va dong lai trong khoi finally;
 * ham ghi (them / sua / xoa) bao quanh bang Transaction, loi thi rollback roi nem lai cho Service xu ly.
 */
public class BaseRepository implements IBaseRepository {

    /** Mo 1 Session moi tu SessionFactory dung chung. Nho dong lai (session.close()) sau khi dung. */
    protected Session moSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    /** Rollback an toan: chi rollback khi transaction con hoat dong. */
    protected void hoanTac(Transaction giaoDich) {
        if (giaoDich != null && giaoDich.isActive()) {
            giaoDich.rollback();
        }
    }

    // ==================== ThuongHieu ====================

    @Override
    public List<ThuongHieu> layTatCaThuongHieu() {
        Session session = moSession();
        try {
            return session.createQuery("from ThuongHieu x order by x.maThuongHieu desc", ThuongHieu.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public ThuongHieu timThuongHieuTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(ThuongHieu.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public ThuongHieu themThuongHieu(ThuongHieu thuongHieu) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(thuongHieu);
            giaoDich.commit();
            return thuongHieu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public ThuongHieu capNhatThuongHieu(ThuongHieu thuongHieu) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ThuongHieu daLuu = (ThuongHieu) session.merge(thuongHieu);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaThuongHieu(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ThuongHieu canXoa = session.get(ThuongHieu.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demThuongHieu() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from ThuongHieu x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== DanhMuc ====================

    @Override
    public List<DanhMuc> layTatCaDanhMuc() {
        Session session = moSession();
        try {
            return session.createQuery("from DanhMuc x order by x.maDanhMuc desc", DanhMuc.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public DanhMuc timDanhMucTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(DanhMuc.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public DanhMuc themDanhMuc(DanhMuc danhMuc) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(danhMuc);
            giaoDich.commit();
            return danhMuc;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public DanhMuc capNhatDanhMuc(DanhMuc danhMuc) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            DanhMuc daLuu = (DanhMuc) session.merge(danhMuc);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaDanhMuc(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            DanhMuc canXoa = session.get(DanhMuc.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demDanhMuc() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from DanhMuc x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== MauSac ====================

    @Override
    public List<MauSac> layTatCaMauSac() {
        Session session = moSession();
        try {
            return session.createQuery("from MauSac x order by x.maMauSac desc", MauSac.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public MauSac timMauSacTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(MauSac.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public MauSac themMauSac(MauSac mauSac) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(mauSac);
            giaoDich.commit();
            return mauSac;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public MauSac capNhatMauSac(MauSac mauSac) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            MauSac daLuu = (MauSac) session.merge(mauSac);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaMauSac(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            MauSac canXoa = session.get(MauSac.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demMauSac() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from MauSac x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== ChuViTayCam ====================

    @Override
    public List<ChuViTayCam> layTatCaChuViTayCam() {
        Session session = moSession();
        try {
            return session.createQuery("from ChuViTayCam x order by x.maChuViTayCam desc", ChuViTayCam.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public ChuViTayCam timChuViTayCamTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(ChuViTayCam.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public ChuViTayCam themChuViTayCam(ChuViTayCam chuViTayCam) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(chuViTayCam);
            giaoDich.commit();
            return chuViTayCam;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public ChuViTayCam capNhatChuViTayCam(ChuViTayCam chuViTayCam) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChuViTayCam daLuu = (ChuViTayCam) session.merge(chuViTayCam);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaChuViTayCam(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChuViTayCam canXoa = session.get(ChuViTayCam.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demChuViTayCam() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from ChuViTayCam x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== KhoiLuong ====================

    @Override
    public List<KhoiLuong> layTatCaKhoiLuong() {
        Session session = moSession();
        try {
            return session.createQuery("from KhoiLuong x order by x.maKhoiLuong desc", KhoiLuong.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public KhoiLuong timKhoiLuongTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(KhoiLuong.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public KhoiLuong themKhoiLuong(KhoiLuong khoiLuong) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(khoiLuong);
            giaoDich.commit();
            return khoiLuong;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public KhoiLuong capNhatKhoiLuong(KhoiLuong khoiLuong) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            KhoiLuong daLuu = (KhoiLuong) session.merge(khoiLuong);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaKhoiLuong(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            KhoiLuong canXoa = session.get(KhoiLuong.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demKhoiLuong() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from KhoiLuong x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== ChatLieu ====================

    @Override
    public List<ChatLieu> layTatCaChatLieu() {
        Session session = moSession();
        try {
            return session.createQuery("from ChatLieu x order by x.maChatLieu desc", ChatLieu.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public ChatLieu timChatLieuTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(ChatLieu.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public ChatLieu themChatLieu(ChatLieu chatLieu) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(chatLieu);
            giaoDich.commit();
            return chatLieu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public ChatLieu capNhatChatLieu(ChatLieu chatLieu) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChatLieu daLuu = (ChatLieu) session.merge(chatLieu);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaChatLieu(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChatLieu canXoa = session.get(ChatLieu.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demChatLieu() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from ChatLieu x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== NhanVien ====================

    @Override
    public List<NhanVien> layTatCaNhanVien() {
        Session session = moSession();
        try {
            return session.createQuery("from NhanVien x order by x.maNhanVien desc", NhanVien.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public NhanVien timNhanVienTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(NhanVien.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public NhanVien themNhanVien(NhanVien nhanVien) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(nhanVien);
            giaoDich.commit();
            return nhanVien;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public NhanVien capNhatNhanVien(NhanVien nhanVien) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            NhanVien daLuu = (NhanVien) session.merge(nhanVien);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaNhanVien(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            NhanVien canXoa = session.get(NhanVien.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demNhanVien() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from NhanVien x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== KhachHang ====================

    @Override
    public List<KhachHang> layTatCaKhachHang() {
        Session session = moSession();
        try {
            return session.createQuery("from KhachHang x order by x.maKhachHang desc", KhachHang.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public KhachHang timKhachHangTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(KhachHang.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public KhachHang themKhachHang(KhachHang khachHang) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(khachHang);
            giaoDich.commit();
            return khachHang;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public KhachHang capNhatKhachHang(KhachHang khachHang) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            KhachHang daLuu = (KhachHang) session.merge(khachHang);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaKhachHang(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            KhachHang canXoa = session.get(KhachHang.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demKhachHang() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from KhachHang x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== SanPham ====================

    @Override
    public List<SanPham> layTatCaSanPham() {
        Session session = moSession();
        try {
            return session.createQuery("from SanPham x order by x.maSanPham desc", SanPham.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public SanPham timSanPhamTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(SanPham.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public SanPham themSanPham(SanPham sanPham) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(sanPham);
            giaoDich.commit();
            return sanPham;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public SanPham capNhatSanPham(SanPham sanPham) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            SanPham daLuu = (SanPham) session.merge(sanPham);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaSanPham(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            SanPham canXoa = session.get(SanPham.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demSanPham() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from SanPham x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== ChiTietSanPham ====================

    @Override
    public List<ChiTietSanPham> layTatCaChiTietSanPham() {
        Session session = moSession();
        try {
            return session.createQuery("from ChiTietSanPham x order by x.maChiTietSanPham desc", ChiTietSanPham.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public ChiTietSanPham timChiTietSanPhamTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(ChiTietSanPham.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public ChiTietSanPham themChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(chiTietSanPham);
            giaoDich.commit();
            return chiTietSanPham;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public ChiTietSanPham capNhatChiTietSanPham(ChiTietSanPham chiTietSanPham) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChiTietSanPham daLuu = (ChiTietSanPham) session.merge(chiTietSanPham);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaChiTietSanPham(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChiTietSanPham canXoa = session.get(ChiTietSanPham.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demChiTietSanPham() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from ChiTietSanPham x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== HinhAnhSanPham ====================

    @Override
    public List<HinhAnhSanPham> layTatCaHinhAnhSanPham() {
        Session session = moSession();
        try {
            return session.createQuery("from HinhAnhSanPham x order by x.maHinhAnhSanPham desc", HinhAnhSanPham.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public HinhAnhSanPham timHinhAnhSanPhamTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(HinhAnhSanPham.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public HinhAnhSanPham themHinhAnhSanPham(HinhAnhSanPham hinhAnhSanPham) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(hinhAnhSanPham);
            giaoDich.commit();
            return hinhAnhSanPham;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public HinhAnhSanPham capNhatHinhAnhSanPham(HinhAnhSanPham hinhAnhSanPham) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            HinhAnhSanPham daLuu = (HinhAnhSanPham) session.merge(hinhAnhSanPham);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaHinhAnhSanPham(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            HinhAnhSanPham canXoa = session.get(HinhAnhSanPham.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demHinhAnhSanPham() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from HinhAnhSanPham x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== ChuongTrinhKhuyenMai ====================

    @Override
    public List<ChuongTrinhKhuyenMai> layTatCaChuongTrinhKhuyenMai() {
        Session session = moSession();
        try {
            return session.createQuery("from ChuongTrinhKhuyenMai x order by x.maChuongTrinhKhuyenMai desc", ChuongTrinhKhuyenMai.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public ChuongTrinhKhuyenMai timChuongTrinhKhuyenMaiTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(ChuongTrinhKhuyenMai.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public ChuongTrinhKhuyenMai themChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(chuongTrinhKhuyenMai);
            giaoDich.commit();
            return chuongTrinhKhuyenMai;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public ChuongTrinhKhuyenMai capNhatChuongTrinhKhuyenMai(ChuongTrinhKhuyenMai chuongTrinhKhuyenMai) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChuongTrinhKhuyenMai daLuu = (ChuongTrinhKhuyenMai) session.merge(chuongTrinhKhuyenMai);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaChuongTrinhKhuyenMai(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChuongTrinhKhuyenMai canXoa = session.get(ChuongTrinhKhuyenMai.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demChuongTrinhKhuyenMai() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from ChuongTrinhKhuyenMai x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== ChiTietChuongTrinhKhuyenMai ====================

    @Override
    public List<ChiTietChuongTrinhKhuyenMai> layTatCaChiTietChuongTrinhKhuyenMai() {
        Session session = moSession();
        try {
            return session.createQuery("from ChiTietChuongTrinhKhuyenMai x order by x.maChiTietChuongTrinhKhuyenMai desc", ChiTietChuongTrinhKhuyenMai.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public ChiTietChuongTrinhKhuyenMai timChiTietChuongTrinhKhuyenMaiTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(ChiTietChuongTrinhKhuyenMai.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public ChiTietChuongTrinhKhuyenMai themChiTietChuongTrinhKhuyenMai(ChiTietChuongTrinhKhuyenMai chiTietChuongTrinhKhuyenMai) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(chiTietChuongTrinhKhuyenMai);
            giaoDich.commit();
            return chiTietChuongTrinhKhuyenMai;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public ChiTietChuongTrinhKhuyenMai capNhatChiTietChuongTrinhKhuyenMai(ChiTietChuongTrinhKhuyenMai chiTietChuongTrinhKhuyenMai) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChiTietChuongTrinhKhuyenMai daLuu = (ChiTietChuongTrinhKhuyenMai) session.merge(chiTietChuongTrinhKhuyenMai);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaChiTietChuongTrinhKhuyenMai(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            ChiTietChuongTrinhKhuyenMai canXoa = session.get(ChiTietChuongTrinhKhuyenMai.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demChiTietChuongTrinhKhuyenMai() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from ChiTietChuongTrinhKhuyenMai x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== PhieuGiamGia ====================

    @Override
    public List<PhieuGiamGia> layTatCaPhieuGiamGia() {
        Session session = moSession();
        try {
            return session.createQuery("from PhieuGiamGia x order by x.maPhieuGiamGia desc", PhieuGiamGia.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public PhieuGiamGia timPhieuGiamGiaTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(PhieuGiamGia.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public PhieuGiamGia themPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(phieuGiamGia);
            giaoDich.commit();
            return phieuGiamGia;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public PhieuGiamGia capNhatPhieuGiamGia(PhieuGiamGia phieuGiamGia) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            PhieuGiamGia daLuu = (PhieuGiamGia) session.merge(phieuGiamGia);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaPhieuGiamGia(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            PhieuGiamGia canXoa = session.get(PhieuGiamGia.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demPhieuGiamGia() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from PhieuGiamGia x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== HoaDon ====================

    @Override
    public List<HoaDon> layTatCaHoaDon() {
        Session session = moSession();
        try {
            return session.createQuery("from HoaDon x order by x.maHoaDon desc", HoaDon.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public HoaDon timHoaDonTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(HoaDon.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public HoaDon themHoaDon(HoaDon hoaDon) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(hoaDon);
            giaoDich.commit();
            return hoaDon;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public HoaDon capNhatHoaDon(HoaDon hoaDon) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            HoaDon daLuu = (HoaDon) session.merge(hoaDon);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaHoaDon(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            HoaDon canXoa = session.get(HoaDon.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demHoaDon() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from HoaDon x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== HoaDonChiTiet ====================

    @Override
    public List<HoaDonChiTiet> layTatCaHoaDonChiTiet() {
        Session session = moSession();
        try {
            return session.createQuery("from HoaDonChiTiet x order by x.maHoaDonChiTiet desc", HoaDonChiTiet.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public HoaDonChiTiet timHoaDonChiTietTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(HoaDonChiTiet.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public HoaDonChiTiet themHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(hoaDonChiTiet);
            giaoDich.commit();
            return hoaDonChiTiet;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public HoaDonChiTiet capNhatHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            HoaDonChiTiet daLuu = (HoaDonChiTiet) session.merge(hoaDonChiTiet);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaHoaDonChiTiet(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            HoaDonChiTiet canXoa = session.get(HoaDonChiTiet.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demHoaDonChiTiet() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from HoaDonChiTiet x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== LichSuTrangThaiHoaDon ====================

    @Override
    public List<LichSuTrangThaiHoaDon> layTatCaLichSuTrangThaiHoaDon() {
        Session session = moSession();
        try {
            return session.createQuery("from LichSuTrangThaiHoaDon x order by x.maLichSuTrangThaiHoaDon desc", LichSuTrangThaiHoaDon.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public LichSuTrangThaiHoaDon timLichSuTrangThaiHoaDonTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(LichSuTrangThaiHoaDon.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public LichSuTrangThaiHoaDon themLichSuTrangThaiHoaDon(LichSuTrangThaiHoaDon lichSuTrangThaiHoaDon) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(lichSuTrangThaiHoaDon);
            giaoDich.commit();
            return lichSuTrangThaiHoaDon;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public LichSuTrangThaiHoaDon capNhatLichSuTrangThaiHoaDon(LichSuTrangThaiHoaDon lichSuTrangThaiHoaDon) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            LichSuTrangThaiHoaDon daLuu = (LichSuTrangThaiHoaDon) session.merge(lichSuTrangThaiHoaDon);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaLichSuTrangThaiHoaDon(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            LichSuTrangThaiHoaDon canXoa = session.get(LichSuTrangThaiHoaDon.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demLichSuTrangThaiHoaDon() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from LichSuTrangThaiHoaDon x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }

    // ==================== SuDungPhieuGiamGia ====================

    @Override
    public List<SuDungPhieuGiamGia> layTatCaSuDungPhieuGiamGia() {
        Session session = moSession();
        try {
            return session.createQuery("from SuDungPhieuGiamGia x order by x.maSuDungPhieuGiamGia desc", SuDungPhieuGiamGia.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public SuDungPhieuGiamGia timSuDungPhieuGiamGiaTheoId(Long id) {
        if (id == null) {
            return null;
        }
        Session session = moSession();
        try {
            return session.get(SuDungPhieuGiamGia.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public SuDungPhieuGiamGia themSuDungPhieuGiamGia(SuDungPhieuGiamGia suDungPhieuGiamGia) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            session.persist(suDungPhieuGiamGia);
            giaoDich.commit();
            return suDungPhieuGiamGia;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public SuDungPhieuGiamGia capNhatSuDungPhieuGiamGia(SuDungPhieuGiamGia suDungPhieuGiamGia) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            SuDungPhieuGiamGia daLuu = (SuDungPhieuGiamGia) session.merge(suDungPhieuGiamGia);
            giaoDich.commit();
            return daLuu;
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public void xoaSuDungPhieuGiamGia(Long id) {
        Session session = moSession();
        Transaction giaoDich = null;
        try {
            giaoDich = session.beginTransaction();
            SuDungPhieuGiamGia canXoa = session.get(SuDungPhieuGiamGia.class, id);
            if (canXoa != null) {
                session.remove(canXoa);
            }
            giaoDich.commit();
        } catch (RuntimeException loi) {
            hoanTac(giaoDich);
            throw loi;
        } finally {
            session.close();
        }
    }

    @Override
    public long demSuDungPhieuGiamGia() {
        Session session = moSession();
        try {
            return session.createQuery("select count(x) from SuDungPhieuGiamGia x", Long.class).uniqueResult();
        } finally {
            session.close();
        }
    }
}
