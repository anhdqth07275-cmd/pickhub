package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ThuongHieu")
@Getter
@Setter
@NoArgsConstructor
public class ThuongHieu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maThuongHieu;

    private String tenThuongHieu;

    private String duongDan;

    private String duongDanLogo;

    private String trangThai;
}
