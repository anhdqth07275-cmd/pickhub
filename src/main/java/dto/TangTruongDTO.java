package dto;

/** Dong nho "+12.5% so voi thang truoc" duoi the so. loai = up (xanh) / down (do) / flat (xam). */
public class TangTruongDTO {
    private final String chu;        // "+12.5%", "-5%", "0%"
    private final String loai;       // up | down | flat
    private final boolean coSoSoSanh; // false khi ky truoc = 0 (khong chia duoc)

    public TangTruongDTO(String chu, String loai, boolean coSoSoSanh) {
        this.chu = chu; this.loai = loai; this.coSoSoSanh = coSoSoSanh;
    }
    public String getChu() { return chu; }
    public String getLoai() { return loai; }
    public boolean isCoSoSoSanh() { return coSoSoSanh; }
}