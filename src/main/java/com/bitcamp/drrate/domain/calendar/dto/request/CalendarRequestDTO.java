package com.bitcamp.drrate.domain.calendar.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarRequestDTO {
    @NotNull
    private Long cal_user_id; // 사용자 ID

    @NotNull
    private String installment_name; // 적금명

    @NotNull
    private String bank_name; // 은행명
    
    @NotNull
    @Min(0)  //금액은 0 이상
    private Long amount; //금액

    @NotNull
    @FutureOrPresent // 현재 or 미래 날짜만 허용
    private LocalDate start_date; // 시작 날짜

    @NotNull
    @Future
    private LocalDate end_date; // 종료 날짜
}
