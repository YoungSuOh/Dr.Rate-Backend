package com.bitcamp.drrate.domain.products.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponseDTO {

    @JsonProperty("result")
    private Result result;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("baseList")
        private List<ProductApiDto> baseList;  // 기본 상품 리스트
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductApiDto {
        @JsonProperty("fin_prdt_cd")
        private String prdCo;

        @JsonProperty("fin_co_no")
        private Long bankCo;

        @JsonProperty("kor_co_nm")
        private String bankName;

        @JsonProperty("fin_prdt_nm")
        private String prdName;

        @JsonProperty("join_way")
        private String joinWay;

        @JsonProperty("mtrt_int")
        private String mtrtInt;

        @JsonProperty("spcl_cnd")
        private String spclCnd;

        @JsonProperty("join_member")
        private String joinMember;

        @JsonProperty("etc_note")
        private String etc;

        @JsonProperty("max_limit")
        private Long max;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProductCondition {
        private String description;
        private double rate;
        private String conditionType;
    }
}
