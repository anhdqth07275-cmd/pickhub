package controller;

import exception.NghiepVuException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.XacThucKhachHangService;
import java.io.IOException;

@WebServlet("/tai-khoan/dang-ky")
public class DangKyKhachHangServlet extends BaseServlet {
    private final XacThucKhachHangService service=new XacThucKhachHangService();
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        req.getRequestDispatcher("/trang-chu-frontend/dang-ky.jsp").forward(req,resp);
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        try{
            service.dangKy(docChuoi(req,"hoTen"),docChuoi(req,"email"),docChuoi(req,"soDienThoai"),req.getParameter("matKhau"),req.getParameter("nhapLai"));
            resp.sendRedirect(req.getContextPath()+"/tai-khoan/dang-nhap?dangKy=1");
        }catch(NghiepVuException e){
            req.setAttribute("loi",e.getMessage());
            req.getRequestDispatcher("/trang-chu-frontend/dang-ky.jsp").forward(req,resp);
        }catch(org.hibernate.exception.ConstraintViolationException e){
            // note code: database co UNIQUE de chan trung lap ke ca khi hai nguoi dang ky cung luc.
            req.setAttribute("loi","Email hoặc số điện thoại đã được sử dụng.");
            req.getRequestDispatcher("/trang-chu-frontend/dang-ky.jsp").forward(req,resp);
        }
    }
}
