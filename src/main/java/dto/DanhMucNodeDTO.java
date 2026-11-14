package dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 1 nut trong cay danh muc (dung cho menu / bo loc storefront va dropdown form san pham).
 * Danh muc goc co danh sach "con"; danh muc con thi "con" rong.
 */
public class DanhMucNodeDTO {
    private final Long ma;
    private final String ten;
    private final List<DanhMucNodeDTO> con = new ArrayList<DanhMucNodeDTO>();

    public DanhMucNodeDTO(Long ma, String ten) {
        this.ma = ma;
        this.ten = ten;
    }
    public Long getMa() { return ma; }
    public String getTen() { return ten; }
    public List<DanhMucNodeDTO> getCon() { return con; }
}
