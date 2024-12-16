package com.bitcamp.drrate.domain.calendar.controller;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;
import com.bitcamp.drrate.domain.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService; // Service와 연결

    /**
     * 달력 이벤트 저장
     * URL: '/api/calendar/save'
     * -시작 날짜와 만기 날짜를 포함한 달력 이벤트 저장
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveCalendarEntry(@RequestBody CalendarRequestDTO request) {
        calendarService.saveCalendarEntry(request);
        return ResponseEntity.ok("이벤트 저장 성공");
    }

    /**
     * 달력 이벤트 목록 가져오기
     * URL: '/api/calendar/events'
     * -적금 은행, 적금 상품명, 시작 날짜, 만기 날짜
     */
    @GetMapping("/events")
    public ResponseEntity<List<CalendarResponseDTO>> getCalendarEvents() {
        List<CalendarResponseDTO> events = calendarService.getCalendarEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * 달력 이벤트 수정
     * URL: '/api/calendar/{id}'
     * -기존 이벤트를 수정하여 업데이트
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCalendarEntry(@PathVariable Long id, @RequestBody CalendarRequestDTO request) {
        calendarService.updateCalendarEntry(id, request);
        return ResponseEntity.ok("수정 완료");
    }
    
    /**
     * 달력 이벤트 삭제
     * URL: '/api/calendar/{id}'
     * 특정 ID에 해당하는 달력 이벤트를 데이터베이스에서 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCalendarEntry(@PathVariable Long id) {
        calendarService.deleteCalendarEntry(id);
        return ResponseEntity.ok("삭제 완료");
    }
}
