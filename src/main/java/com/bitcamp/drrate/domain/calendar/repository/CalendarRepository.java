package com.bitcamp.drrate.domain.calendar.repository;

import com.bitcamp.drrate.domain.calendar.entity.Calendar;

import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    
    @Query("SELECT c FROM Calendar c WHERE c.groupId = :groupId")
    List<Calendar> findAllByGroupId(@Param("groupId") String groupId); 

    @Query("SELECT c FROM Calendar c WHERE c.cal_user_id = :userId")
    List<Calendar> findByCalUserId(@Param("userId") Long userId); // 로그인한 사용자에 맞는 일정만 가져오기
    
    // 수정
    @Transactional
    @Modifying
    @Query("UPDATE Calendar c SET c.installment_name = :installmentName, c.bank_name = :bankName, c.amount = :amount, c.end_date = :endDate WHERE c.groupId = :groupId")
    void updateByGroupId(@Param("groupId") String groupId, 
                         @Param("installmentName") String installmentName, 
                         @Param("bankName") String bankName, 
                         @Param("amount") Long long1,
    					 @Param("endDate") LocalDate localDate);
   
    // 삭제
    @Transactional
    @Modifying
    @Query("DELETE FROM Calendar c WHERE c.groupId = :groupId")
    void deleteByGroupId(@Param("groupId") String groupId); 
}



