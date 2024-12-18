package com.bitcamp.drrate.domain.visitor.service;

import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.visitor.entity.DailyVisitor;

import java.time.LocalDate;
import java.util.List;

public interface VisitorService {
    void trackVisit(CustomUserDetails userDetails, String guestId);

    Long getTodayMembersCount();

    Long getTodayGuestCount();

    Long getTodayNewMembersCount();

    Long getTodayInquiriesCount();

    List<DailyVisitor> getLast4DaysVisitorCounts();

    int getLast7DaysMemberVisitorCount();

    int getLast7DaysGuestVisitorCount();

    int getLast7DaysTotalVisitorCount();

    int getLast7DaysNewMembersCount();

    int getLast7DaysInquiriesCount();

    int getThisMonthTotalMemberCount();

    int getThisMonthGuestVisitorCount();

    int getThisMonthVisitorTotalCount();

    int getThisMonthNewMembersCount();

    int getThisMonthInquiriesCount();


}