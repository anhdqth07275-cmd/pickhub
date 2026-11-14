package dto;

/**
 * Bo loc dung chung cho cac man hinh danh sach (tim kiem + loc trang thai + phan trang).
 * Cac cot (cotTim, cotTrangThai, dieuKienThem, sapXep) do CODE truyen vao, khong lay tu nguoi dung
 * -> tranh loi HQL injection. Chi "tuKhoa" va "trangThai" la du lieu nguoi dung nhap (da dung tham so :tk, :tt).
 */
public class BoLoc {
    private String cotTim;          // vd "tenThuongHieu" hoac "hoTen,thuDienTu" (nhieu cot cach nhau dau phay)
    private String tuKhoa;
    private String cotTrangThai;    // vd "trangThai"
    private String trangThai;
    private String dieuKienThem;    // vd "x.trangThai <> 'GIO_HANG'"
    private String sapXep;          // vd "x.soLuongTon asc"; null = sap theo khoa chinh giam dan
    private int trang = 1;
    private int kichThuoc = 10;

    public BoLoc cotTim(String v) { this.cotTim = v; return this; }
    public BoLoc tuKhoa(String v) { this.tuKhoa = v; return this; }
    public BoLoc cotTrangThai(String v) { this.cotTrangThai = v; return this; }
    public BoLoc trangThai(String v) { this.trangThai = v; return this; }
    public BoLoc dieuKienThem(String v) { this.dieuKienThem = v; return this; }
    public BoLoc sapXep(String v) { this.sapXep = v; return this; }
    public BoLoc trang(int v) { this.trang = Math.max(v, 1); return this; }
    public BoLoc kichThuoc(int v) { this.kichThuoc = Math.max(v, 1); return this; }

    public String getCotTim() { return cotTim; }
    public String getTuKhoa() { return tuKhoa; }
    public String getCotTrangThai() { return cotTrangThai; }
    public String getTrangThai() { return trangThai; }
    public String getDieuKienThem() { return dieuKienThem; }
    public String getSapXep() { return sapXep; }
    public int getTrang() { return trang; }
    public int getKichThuoc() { return kichThuoc; }
}
