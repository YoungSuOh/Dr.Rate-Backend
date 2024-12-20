package com.bitcamp.drrate.domain.calendar.controller;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;
import com.bitcamp.drrate.domain.calendar.service.CalendarService;
import com.bitcamp.drrate.global.ApiResponse;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus;
import com.bitcamp.drrate.global.code.resultCode.SuccessStatus;
import com.bitcamp.drrate.global.exception.exceptionhandler.CalendarServiceExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService; // CalendarService와 연결

    // 달력 이벤트 저장
    @PostMapping("/save")
    public ApiResponse<HttpStatus> saveCalendarEntry(@RequestBody CalendarRequestDTO request) {
        try {
            calendarService.saveCalendarEntry(request);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.CALENDAR_SAVE_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_SAVE_FAILED.getCode(), ErrorStatus.CALENDAR_SAVE_FAILED.getMessage(), null);
        }
    }

    // 목록 가져오기
    @GetMapping("/events")
    public ApiResponse<List<CalendarResponseDTO>> getCalendarEvents() {
        try {
            List<CalendarResponseDTO> events = calendarService.getCalendarEvents();
            return ApiResponse.onSuccess(events, SuccessStatus.CALENDAR_QUERY_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_QUERY_FAILED.getCode(), ErrorStatus.CALENDAR_QUERY_FAILED.getMessage(), null);
        }
    }

    // 수정
    @PutMapping("/update/{id}")
    public ApiResponse<HttpStatus> updateCalendarEntry(@PathVariable("id") Long id, @RequestBody CalendarRequestDTO request) {
        try {
            calendarService.updateCalendarEntry(id, request);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.CALENDAR_UPDATE_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_UPDATE_FAILED.getCode(), ErrorStatus.CALENDAR_UPDATE_FAILED.getMessage(), null);
        }
    }

    // 삭제
    @DeleteMapping("/delete/{id}")
    public ApiResponse<HttpStatus> deleteCalendarEntry(@PathVariable("id") Long id) {
        try {
            calendarService.deleteCalendarEntry(id);
            return ApiResponse.onSuccess(HttpStatus.OK, SuccessStatus.CALENDAR_DELETE_SUCCESS);
        } catch (CalendarServiceExceptionHandler e) {
            return ApiResponse.onFailure(e.getErrorReason().getCode(), e.getErrorReason().getMessage(), null);
        } catch (Exception e) {
            return ApiResponse.onFailure(ErrorStatus.CALENDAR_DELETE_FAILED.getCode(), ErrorStatus.CALENDAR_DELETE_FAILED.getMessage(), null);
        }
    }
}
