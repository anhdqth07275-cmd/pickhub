package entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Lop cha cho cac entity co 2 cot ngayTao, ngayCapNhat.
 * LichSuTrangThaiHoaDon KHONG ke thua vi bang do khong co 2 cot nay.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(name = "ngayTao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngayCapNhat")
    private LocalDateTime ngayCapNhat;

    // note code nhu nay nhe ban: tu dong gan ngay khi them moi (DB co DEFAULT nhung Hibernate van insert NULL nen phai gan o day)
    @PrePersist
    protected void truocKhiThem() {
        LocalDateTime bayGio = LocalDateTime.now();
        if (ngayTao == null) ngayTao = bayGio;
        if (ngayCapNhat == null) ngayCapNhat = bayGio;
    }

    // note code nhu nay nhe ban: tu dong cap nhat ngayCapNhat moi lan sua
    @PreUpdate
    protected void truocKhiSua() {
        ngayCapNhat = LocalDateTime.now();
    }
}
