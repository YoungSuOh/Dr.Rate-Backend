package com.bitcamp.drrate.domain.products.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class InsResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionApiDto {
        @JsonProperty("dcls_month")
        private String dclsMonth;

        @JsonProperty("fin_co_no")
        private String finCoNo;

        @JsonProperty("fin_prdt_cd")
        private String finPrdtCd;

        @JsonProperty("intr_rate_type")
        private String intrRateType;

        @JsonProperty("intr_rate_type_nm")
        private String intrRateTypeNm;

        @JsonProperty("rsrv_type")
        private String rsrvType;

        @JsonProperty("rsrv_type_nm")
        private String rsrvTypeNm;

        @JsonProperty("save_trm")
        private String saveTrm;

        @JsonProperty("intr_rate")
        private Double intrRate;  // 이자율은 Double로 처리

        @JsonProperty("intr_rate2")
        private Double intrRate2; // 두 번째 이자율도 Double로 처리

        // 기본 이자율과 특별 이자율을 BigDecimal로 변환하여 사용하는 메서드
        public BigDecimal getBasicRate() {
            return intrRate == null ? null : BigDecimal.valueOf(intrRate);
        }

        public BigDecimal getSpclRate() {
            return intrRate2 == null ? null : BigDecimal.valueOf(intrRate2);
        }
    }
}
