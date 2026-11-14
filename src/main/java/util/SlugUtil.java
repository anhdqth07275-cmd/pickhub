package util;

import java.text.Normalizer;

/**
 * Tao duong dan (slug) khong dau tu ten. Vd: "Franklin Sports" -> "franklin-sports".
 */
public class SlugUtil {

    public static String taoSlug(String ten) {
        if (ten == null) return "";
        String s = ten.replace('đ', 'd').replace('Đ', 'D');
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", ""); // bo dau tieng Viet
        s = s.toLowerCase().replaceAll("[^a-z0-9]+", "-");                          // ky tu la -> dau gach
        return s.replaceAll("^-+|-+$", "");
    }
}
