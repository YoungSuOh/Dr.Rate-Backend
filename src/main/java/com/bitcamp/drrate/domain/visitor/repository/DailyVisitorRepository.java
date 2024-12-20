package com.bitcamp.drrate.domain.visitor.repository;

import com.bitcamp.drrate.domain.visitor.entity.DailyVisitor;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyVisitorRepository extends JpaRepository<DailyVisitor, LocalDate> {

    @Query("SELECT d FROM DailyVisitor d WHERE d.visitDate < :today ORDER BY d.visitDate DESC LIMIT 4")
    List<DailyVisitor> findLast4DaysVisitors(@Param("today") LocalDate today);

    @Query("SELECT SUM(d.totalVisitorsCount) FROM DailyVisitor d " +
            "WHERE d.visitDate BETWEEN :startDate AND :endDate")
    Integer findTotalVisitorsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(d.memberVisitorsCount) FROM DailyVisitor d " +
            "WHERE d.visitDate BETWEEN :startDate AND :endDate")
    Integer findTotalMemberVisitorsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(d.guestVisitorsCount) FROM DailyVisitor d " +
            "WHERE d.visitDate BETWEEN :startDate AND :endDate")
    Integer findTotalGuestVisitorsBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(d.newMembersCount) FROM DailyVisitor d " +
            "WHERE d.visitDate BETWEEN :startDate AND :endDate")
    Integer findTotalNewMembersBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
