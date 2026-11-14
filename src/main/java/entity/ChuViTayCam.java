package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "ChuViTayCam")
@Getter
@Setter
@NoArgsConstructor
public class ChuViTayCam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChuViTayCam;

    private BigDecimal chuViInch;

    private String trangThai;
}
