package com.bitcamp.drrate.domain.visitor.controller;

import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.domain.visitor.entity.DailyVisitor;
import com.bitcamp.drrate.domain.visitor.service.VisitorService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.UsersServiceExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping("healthCheck")
    public ResponseEntity<HttpStatus> healthCheck() {
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/trackVisit")
    public ApiResponse<HttpStatus> trackVisit(@RequestBody Map<String, String> payload,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            visitorService.trackVisit(customUserDetails, payload.get("guestId"));
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.VISIT_RECORD_SAVE_SUCCESS);
        } catch (UsersServiceExceptionHandler e) {
            return ApiResponse.onFailure(ErrorStatus.USER_ID_CANNOT_FOUND.getCode(),
                    ErrorStatus.USER_ID_CANNOT_FOUND.getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.VISIT_RECORD_FAILED.getCode(),
                    ErrorStatus.VISIT_RECORD_FAILED.getMessage(), null);
        }
    }

    @GetMapping("/admin/visitor-summary")
    public ApiResponse<Map<String, Object>> getVisitorSummary() {
        try {
            Map<String, Object> result = new HashMap<>();

            // Redis에서 오늘 방문자 수, 신규 가입자 수, 문의 수 가져오기
            Long todayMembersCount = visitorService.getTodayMembersCount();
            Long todayGuestCount = visitorService.getTodayGuestCount();
            Long todayNewMembersCount = visitorService.getTodayNewMembersCount();
            Long todayTotalMembersCount = todayMembersCount + todayGuestCount;;


            // MySQL에서 최근 4일 방문자 수, 가입자 수, 문의 수 가져오기
            List<DailyVisitor> last4DaysVisitors = visitorService.getLast4DaysVisitorCounts();

            // MySQL에서 최근 7일 및 이번 달 합계 가져오기
            int last7DaysMemberCount = visitorService.getLast7DaysMemberVisitorCount();
            int last7DaysGuestCount = visitorService.getLast7DaysGuestVisitorCount();
            int last7DaysTotalCount = visitorService.getLast7DaysTotalVisitorCount();
            int last7DaysNewMembersCount = visitorService.getLast7DaysNewMembersCount();


            int thisMonthMemberCount =  visitorService.getThisMonthTotalMemberCount();
            int thisMonthGuestCount = visitorService.getThisMonthGuestVisitorCount();
            int thisMonthTotalCount = visitorService.getThisMonthVisitorTotalCount();
            int thisMonthNewMembersCount = visitorService.getThisMonthNewMembersCount();


            // 데이터 병합
            result.put("today", Map.of(
                    "totalMembersCount", todayTotalMembersCount,
                    "membersCount", todayMembersCount,
                    "todayGuestCount", todayGuestCount,
                    "newMembersCount", todayNewMembersCount
            ));
            result.put("last4Days", last4DaysVisitors);
            result.put("last7DaysTotal", Map.of(
                    "visitorMemberCount", last7DaysMemberCount,
                    "visitorGuestCount", last7DaysGuestCount,
                    "visitorTotalCount", last7DaysTotalCount,
                    "newMembersCount", last7DaysNewMembersCount
            ));
            result.put("thisMonthTotal", Map.of(
                    "visitorMemberCount", thisMonthMemberCount,
                    "visitorGuestCount", thisMonthGuestCount,
                    "visitorTotalCount", thisMonthTotalCount,
                    "newMembersCount", thisMonthNewMembersCount
            ));

            return ApiResponse.onSuccess(result, SuccessStatus.VISIT_RECORD_LOAD_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.VISIT_RECORD_LOAD_FAILED.getCode(),
                    ErrorStatus.VISIT_RECORD_LOAD_FAILED.getMessage(), null);
        }
    }

}
