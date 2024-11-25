package com.bitcamp.drrate.domain.products.entity;

import com.bitcamp.drrate.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "dep_options") // 엔티티 테이블 이름 지정 필수 !!
@Data
public class DepositeOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true) // Allow Null이 Y
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dep_prd_id")
    @Column(name="dep_prd_id")
    private Products products;

    @Column(name = "rate_type", length = 4, nullable = true)
    private String rateType;

    @Column(name = "rate_type_ko", length = 10, nullable = true)
    private String rateTypeKo;

    @Column(name = "save_time", nullable = true)
    private Integer saveTime;

    @Column(name = "basic_rate", precision = 20, scale = 2, nullable = true)
    private BigDecimal basicRate;

    @Column(name = "spcl_rate", precision = 20, scale = 2, nullable = true)
    private BigDecimal spclRate;
}
