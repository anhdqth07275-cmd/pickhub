package controller;

import dto.TaiKhoanKhachHang;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import repository.KhachHangRepository;
import service.XacThucKhachHangService;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/tai-khoan/ho-so")
public class HoSoKhachHangServlet extends BaseServlet {
    private final KhachHangRepository repo=new KhachHangRepository();
    private final XacThucKhachHangService service=new XacThucKhachHangService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        TaiKhoanKhachHang tk=layTaiKhoan(req);if(tk==null){resp.sendRedirect(req.getContextPath()+"/tai-khoan/dang-nhap");return;}
        req.setAttribute("kh",repo.timTheoId(tk.getId()));req.getRequestDispatcher("/trang-chu-frontend/ho-so.jsp").forward(req,resp);
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        TaiKhoanKhachHang tk=layTaiKhoan(req);if(tk==null){resp.sendRedirect(req.getContextPath()+"/tai-khoan/dang-nhap");return;}
        try{
            if("doi-mat-khau".equals(docChuoi(req,"hanhDong"))){
                service.doiMatKhau(tk.getId(),req.getParameter("matKhauCu"),req.getParameter("matKhauMoi"),req.getParameter("nhapLai"));
                resp.sendRedirect(req.getContextPath()+"/tai-khoan/ho-so?doiMatKhau=1");return;
            }
            String ns=docChuoi(req,"ngaySinh");
            service.capNhatHoSo(tk.getId(),docChuoi(req,"hoTen"),docChuoi(req,"soDienThoai"),ns.isEmpty()?null:LocalDate.parse(ns));
            entity.KhachHang khMoi=repo.timTheoId(tk.getId());req.getSession().setAttribute("khachHang",new TaiKhoanKhachHang(khMoi.getMaKhachHang(),khMoi.getHoTen(),khMoi.getThuDienTu(),khMoi.getDuongDanAnhDaiDien()));
            resp.sendRedirect(req.getContextPath()+"/tai-khoan/ho-so?capNhat=1");
        }catch(Exception e){
            req.setAttribute("loi",e instanceof NghiepVuException?e.getMessage():"Dữ liệu không hợp lệ");
            req.setAttribute("kh",repo.timTheoId(tk.getId()));req.getRequestDispatcher("/trang-chu-frontend/ho-so.jsp").forward(req,resp);
        }
    }
    private TaiKhoanKhachHang layTaiKhoan(HttpServletRequest req){
        HttpSession s=req.getSession(false);
        if(s==null) return null;
        Object o=s.getAttribute("khachHang");
        if(o instanceof TaiKhoanKhachHang) return (TaiKhoanKhachHang)o;
        // note code nhu nay nhe ban: khach dang nhap bang /dang-nhap (KhachHangDangNhap) van vao duoc trang ho so
        if(o instanceof dto.KhachHangDangNhap){
            dto.KhachHangDangNhap k=(dto.KhachHangDangNhap)o;
            return new TaiKhoanKhachHang(k.getId(),k.getHoTen(),k.getEmail(),null);
        }
        return null;
    }
}
