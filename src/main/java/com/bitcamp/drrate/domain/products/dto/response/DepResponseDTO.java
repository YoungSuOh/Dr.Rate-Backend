package com.bitcamp.drrate.domain.products.dto.response;

import com.bitcamp.drrate.domain.products.entity.Products;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepResponseDTO {

    @JsonProperty("result")
    private Result result;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("optionList")
        private List<DepResponseDTO.OptionApiDto> optionList;
    }

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

        @JsonProperty("save_trm")
        private String saveTrm;

        @JsonProperty("intr_rate")
        private Double intrRate;  // 이자율은 Double로 처리

        @JsonProperty("intr_rate2")
        private Double intrRate2; // 두 번째 이자율도 Double로 처리

        public BigDecimal getBasicRate() {
            return intrRate == null ? null : BigDecimal.valueOf(intrRate);
        }

        public BigDecimal getSpclRate() {
            return intrRate2 == null ? null : BigDecimal.valueOf(intrRate2);
        }
    }

}
