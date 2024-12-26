package com.bitcamp.drrate.domain.products.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
@JsonIgnoreProperties({"depOptions"})
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true)
    private Long id;
    
    //241211 옵션 - 오혜진
    @OneToMany(mappedBy = "products", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepositeOptions> depOptions; // 연관된 DepositeOptions 목록

    @Column(name = "ctg", length = 4, nullable = true)
    private String ctg;

    @Column(name = "prd_co", columnDefinition = "TEXT", nullable = true)
    private String prdCo;

    @Column(name = "bank_co", nullable = true)
    private Long bankCo;

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

    @Column(name = "join_member", length = 300, nullable = true)
    private String joinMember;

    @Column(name = "etc", columnDefinition = "TEXT", nullable = true)
    private String etc;

    @Column(name = "max", nullable = true)
    private Long max;

    @Column(name = "url", length = 3000, nullable = true)
    private String url;
}
