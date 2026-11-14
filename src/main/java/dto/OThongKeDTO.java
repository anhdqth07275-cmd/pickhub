package dto;

/** 1 o "Tong quan du lieu": ten bang/module, so dong, duong dan module. */
public class OThongKeDTO {
    private final String ten;
    private final long soDong;
    private final String duongDan;

    public OThongKeDTO(String ten, long soDong, String duongDan) {
        this.ten = ten; this.soDong = soDong; this.duongDan = duongDan;
    }
    public String getTen() { return ten; }
    public long getSoDong() { return soDong; }
    public String getDuongDan() { return duongDan; }
}
