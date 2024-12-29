package com.bitcamp.drrate.domain.products.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
@JsonIgnoreProperties({"depOptions", "insOptions"})
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    
    //241211 옵션 - 오혜진
    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepositeOptions> depOptions; // 연관된 DepositeOptions 목록

    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstallMentOptions> insOptions;

    @Column(name = "ctg", length = 4, nullable = true)
    private String ctg;

    @Column(name = "prd_co", columnDefinition = "TEXT", nullable = true)
    private String prdCo;

    @Column(name = "bank_co", nullable = true)
    private Long bankCo;

    @Column(name = "join_member_age", nullable = true)
    private Integer joinMemberAge;

    @Column(name = "bank_name", length = 300, nullable = true)
    private String bankName;

    @Column(name = "bank_logo", length = 255, nullable = true)
    private String bankLogo;

    @Column(name = "prd_name", columnDefinition = "TEXT", nullable = true)
    private String prdName;

    @Column(name = "join_way", length = 300, nullable = true)
    private String joinWay;

    @Column(name = "mtrt_int", columnDefinition = "TEXT", nullable = true)
    private String mtrtInt;

    @Column(name = "spcl_cnd", columnDefinition = "TEXT", nullable = true)
    private String spclCnd;

    @Column(name = "etc", nullable = true)
    private Integer etc;

    @Column(name = "max", nullable = true)
    private Long max;

    @Column(name = "url", length = 3000, nullable = true)
    private String url;
}
