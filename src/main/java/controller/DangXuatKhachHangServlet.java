package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/tai-khoan/dang-xuat")
public class DangXuatKhachHangServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        HttpSession session=req.getSession(false);if(session!=null){session.removeAttribute("khachHang");
            // note code: chi xoa session khach hang, khong lam dang xuat nhan vien neu cung session.
            if(session.getAttribute("taiKhoan")==null)session.invalidate();}
        resp.sendRedirect(req.getContextPath()+"/tai-khoan/dang-nhap?dangXuat=1");
    }
}
