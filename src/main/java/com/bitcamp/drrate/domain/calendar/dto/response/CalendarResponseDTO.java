package com.bitcamp.drrate.domain.calendar.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarResponseDTO {
    private String depositName;  // 예금 이름
    private String bankName;     // 은행 이름
    private Double interestRate; // 이율
}
