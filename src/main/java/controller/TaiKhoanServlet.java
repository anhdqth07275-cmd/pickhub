package controller;

import dto.TaiKhoanDangNhap;
import entity.NhanVien;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import repository.NhanVienRepository;
import util.MatKhauUtil;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/wp-admin/tai-khoan")
public class TaiKhoanServlet extends BaseServlet {
    private final NhanVienRepository repo=new NhanVienRepository();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        NhanVien nv=repo.timTheoId(taiKhoan(req).getId());req.setAttribute("nv",nv);hienThi(req,resp,"tai-khoan","tai-khoan","Tài khoản cá nhân");
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        TaiKhoanDangNhap tk=taiKhoan(req);NhanVien nv=repo.timTheoId(tk.getId());if(nv==null){chuyenHuongLoi(req,resp,"/wp-admin/tai-khoan","Không tìm thấy tài khoản");return;}
        String action=docChuoi(req,"hanhDong");
        try{
            if("cap-nhat-ho-so".equals(action)){String ten=docChuoi(req,"hoTen"),sdt=docChuoi(req,"soDienThoai"),ns=docChuoi(req,"ngaySinh");
                if(ten.length()<2||ten.length()>100)throw new NghiepVuException("Họ tên phải từ 2 đến 100 ký tự");
                if(!sdt.matches("^[0-9]{9,15}$"))throw new NghiepVuException("Số điện thoại không hợp lệ");
                if(repo.daTonTai("soDienThoai",sdt,nv.getMaNhanVien()))throw new NghiepVuException("Số điện thoại đã được sử dụng");
                LocalDate ngaySinh = ns.isEmpty() ? null : LocalDate.parse(ns);
                if (ngaySinh != null && ngaySinh.isAfter(LocalDate.now())) throw new NghiepVuException("Ngày sinh không được ở tương lai");
                nv.setHoTen(ten);nv.setSoDienThoai(sdt);nv.setNgaySinh(ngaySinh);repo.capNhat(nv);
                req.getSession().setAttribute("taiKhoan",new TaiKhoanDangNhap(nv.getMaNhanVien(),nv.getHoTen(),nv.getThuDienTu(),nv.getVaiTro(),nv.getDuongDanAnhDaiDien()));
                chuyenHuong(req,resp,"/wp-admin/tai-khoan","Đã cập nhật hồ sơ");
            }else if("doi-mat-khau".equals(action)){String cu=req.getParameter("matKhauCu"),moi=req.getParameter("matKhauMoi"),lap=req.getParameter("nhapLai");
                if(!MatKhauUtil.kiemTra(cu,nv.getMatKhauMaHoa()))throw new NghiepVuException("Mật khẩu hiện tại không đúng");
                if(moi==null||moi.length()<8||!moi.matches(".*[A-Za-z].*")||!moi.matches(".*[0-9].*"))throw new NghiepVuException("Mật khẩu mới phải có ít nhất 8 ký tự, gồm chữ và số");
                if(!moi.equals(lap))throw new NghiepVuException("Mật khẩu nhập lại không khớp");
                nv.setMatKhauMaHoa(MatKhauUtil.bam(moi));nv.setSoLanDangNhapSai(0);repo.capNhat(nv);
                chuyenHuong(req,resp,"/wp-admin/tai-khoan","Đổi mật khẩu thành công");
            }else throw new NghiepVuException("Hành động không hợp lệ");
        }catch(NghiepVuException e){chuyenHuongLoi(req,resp,"/wp-admin/tai-khoan",e.getMessage());}
        catch(Exception e){chuyenHuongLoi(req,resp,"/wp-admin/tai-khoan","Dữ liệu không hợp lệ");}
    }
}
