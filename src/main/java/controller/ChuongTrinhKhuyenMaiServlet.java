package controller;

import entity.ChuongTrinhKhuyenMai;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.ChuongTrinhKhuyenMaiRepository;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "ChuongTrinhKhuyenMaiServlet", urlPatterns = {
        "/khuyen-mai", "/khuyen-mai/add", "/khuyen-mai/update", "/khuyen-mai/delete"
})
public class ChuongTrinhKhuyenMaiServlet extends HttpServlet {
    private final ChuongTrinhKhuyenMaiRepository repo = new ChuongTrinhKhuyenMaiRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.contains("/delete")) {
            Integer id = Integer.parseInt(req.getParameter("id"));
            repo.delete(id);
            resp.sendRedirect(req.getContextPath() + "/khuyen-mai");
            return;
        }
        req.setAttribute("listKhuyenMai", repo.getAll());
        req.getRequestDispatcher("/trang-chu/pickhub-admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String idStr = req.getParameter("id");

        ChuongTrinhKhuyenMai ctkm = new ChuongTrinhKhuyenMai();
        if (idStr != null && !idStr.isEmpty()) {
            ctkm.setId(Integer.parseInt(idStr));
        }
        ctkm.setMaKhuyenMai(req.getParameter("maKhuyenMai"));
        ctkm.setTenChuongTrinh(req.getParameter("tenChuongTrinh"));
        ctkm.setNgayBatDau(LocalDateTime.parse(req.getParameter("ngayBatDau")));
        ctkm.setNgayKetThuc(LocalDateTime.parse(req.getParameter("ngayKetThuc")));
        ctkm.setTrangThai(req.getParameter("trangThai"));
        ctkm.setMoTa(req.getParameter("moTa"));

        repo.saveOrUpdate(ctkm);
        resp.sendRedirect(req.getContextPath() + "/khuyen-mai");
    }
}