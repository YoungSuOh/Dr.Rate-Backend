package com.bitcamp.drrate.domain.calendar.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "calendar")
@Data
@NoArgsConstructor
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cal_user_id", nullable = false)
    private Long calUserId;

    @Column(name = "cal_installment_id", nullable = false)
    private Long calInstallmentId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder
    public Calendar(Long calUserId, Long calInstallmentId, Long amount, LocalDate startDate, LocalDate endDate) {
        this.calUserId = calUserId;
        this.calInstallmentId = calInstallmentId;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
