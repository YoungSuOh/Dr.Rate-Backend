package com.bitcamp.drrate.domain.calendar.controller;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;
import com.bitcamp.drrate.domain.calendar.service.CalendarService;
import com.bitcamp.drrate.domain.users.dto.CustomUserDetails;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.CalendarServiceExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService; // CalendarService와 연결

    // 달력 이벤트 저장
    @PostMapping("/save")
    public ApiResponse<HttpStatus> saveCalendarEntries(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody List<CalendarRequestDTO> requests) {
        try {
            Long userId = userDetails.getId();
            requests.forEach(request -> request.setCal_user_id(userId));
            calendarService.saveCalendarEntries(requests);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.CALENDAR_SAVE_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_SAVE_FAILED.getCode(), ErrorStatus.CALENDAR_SAVE_FAILED.getMessage(), null);
        }
    }

    // 목록 가져오기
    @GetMapping("/events")
    public ApiResponse<List<CalendarResponseDTO>> getCalendarEvents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getId();
            List<CalendarResponseDTO> events = calendarService.getCalendarEvents(userId);
            return ApiResponse.onSuccess(events, SuccessStatus.CALENDAR_QUERY_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_QUERY_FAILED.getCode(), ErrorStatus.CALENDAR_QUERY_FAILED.getMessage(), null);
        }
    }

    // 수정
    @PutMapping("/update/group/{id}")
    public ApiResponse<HttpStatus> updateCalendarGroup(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("id") Long id, @RequestBody CalendarRequestDTO request) {
        try {
            Long userId = userDetails.getId();
            calendarService.updateCalendarGroup(id, userId, request);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.CALENDAR_UPDATE_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_UPDATE_FAILED.getCode(), ErrorStatus.CALENDAR_UPDATE_FAILED.getMessage(), null);
        }
    }

    // 삭제
    @DeleteMapping("/delete/group/{id}")
    public ApiResponse<HttpStatus> deleteCalendarGroup(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("id") Long id) {
        try {
            Long userId = userDetails.getId();
            calendarService.deleteCalendarGroup(id, userId);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.CALENDAR_DELETE_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_DELETE_FAILED.getCode(), ErrorStatus.CALENDAR_DELETE_FAILED.getMessage(), null);
        }
    }
    
    // 은행명 및 로고 가져오기
    @GetMapping("/banks")
    public List<Map<String, String>> getDistinctBankNamesAndLogos() {
        List<Map<String, String>> banks = calendarService.getDistinctBankNamesAndLogos();
        return banks;
    }

    // 특정 은행의 적금명 가져오기
    @GetMapping("/banks/{bankName}/products")
    public List<String> getProductNamesByBankName(@PathVariable("bankName") String bankName) {
        List<String> productNames = calendarService.getProductNamesByBankName(bankName);
        return productNames;
    }
}
