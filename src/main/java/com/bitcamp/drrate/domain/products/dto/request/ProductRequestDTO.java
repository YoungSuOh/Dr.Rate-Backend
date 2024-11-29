package com.bitcamp.drrate.domain.products.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ProductRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductApiRequest {
        private String conditionDescription;  // 우대조건 설명
        private double interestRate;  // 우대금리
        private String rateDescription;  // 우대금리에 대한 세부 설명
    }
}
