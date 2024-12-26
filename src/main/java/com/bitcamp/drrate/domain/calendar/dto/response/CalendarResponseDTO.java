package com.bitcamp.drrate.domain.calendar.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarResponseDTO {
	private Long id; // 이벤트 ID
	private String bank_name; // 은행명
	private String bank_Logo; // 은행 로고
	private String installment_name; // 적금명
	private LocalDate start_date; // 시작 날짜
	private LocalDate end_date; // 종료 날짜
	private Long amount; // 금액
    private LocalDate fixedStartDate; // 최초 시작일
}