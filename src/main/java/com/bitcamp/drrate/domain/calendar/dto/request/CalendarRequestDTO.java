package com.bitcamp.drrate.domain.calendar.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CalendarRequestDTO {
    private Long calUserId;         // 사용자 ID
    private Long calInstallmentId; // 적금 ID
    private Long amount;           // 납입 금액
    private LocalDate startDate;   // 시작일
    private LocalDate endDate;     // 만기일
}
