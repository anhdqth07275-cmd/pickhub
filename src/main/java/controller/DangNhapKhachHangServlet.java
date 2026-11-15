package controller;

import dto.TaiKhoanKhachHang;
import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import service.XacThucKhachHangService;
import java.io.IOException;

@WebServlet("/tai-khoan/dang-nhap")
public class DangNhapKhachHangServlet extends BaseServlet {
    private final XacThucKhachHangService service=new XacThucKhachHangService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        req.setAttribute("thongBao", "1".equals(req.getParameter("dangXuat")) ? "Bạn đã đăng xuất." : null);
        req.getRequestDispatcher("/trang-chu-frontend/dang-nhap.jsp").forward(req,resp);
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        String email=docChuoi(req,"email");
        try{
            TaiKhoanKhachHang tk=service.dangNhap(email,req.getParameter("matKhau"));
            HttpSession old=req.getSession(false);if(old!=null)old.invalidate();
            HttpSession session=req.getSession(true);session.setAttribute("khachHang",tk);session.setMaxInactiveInterval(30*60);
            resp.sendRedirect(req.getContextPath()+"/");
        }catch(NghiepVuException e){req.setAttribute("loi",e.getMessage());req.setAttribute("email",email);req.getRequestDispatcher("/trang-chu-frontend/dang-nhap.jsp").forward(req,resp);}
    }
}
