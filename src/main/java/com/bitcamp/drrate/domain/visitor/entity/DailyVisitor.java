package com.bitcamp.drrate.domain.visitor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_visitors")
@AllArgsConstructor
@Getter @Setter
@NoArgsConstructor
public class DailyVisitor {
    @Id
    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "member_visitors_count")
    private int memberVisitorsCount;

    @Column(name = "guest_visitors_count")
    private int guestVisitorsCount;

    @Column(name = "total_visitors_count")
    private int totalVisitorsCount;

    @Column(name = "new_members_count")
    private int newMembersCount;
}