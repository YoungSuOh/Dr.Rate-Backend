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
    private Long cal_user_id;

    @NotNull
    private String installment_name;

    @NotNull
    private String bank_name;

    @NotNull
    @Min(0)
    private Long amount;

    @NotNull
    @FutureOrPresent
    private LocalDate start_date;

    @NotNull
    @Future
    private LocalDate end_date;
}