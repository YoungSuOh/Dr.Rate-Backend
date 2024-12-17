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
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService; // CalendarService와 연결
    
    // 달력 이벤트 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveCalendarEntry(@RequestBody CalendarRequestDTO request) {
        calendarService.saveCalendarEntry(request);
        return ResponseEntity.ok("이벤트 저장 성공"); // 성공 메세지
    }

    // 목록 가져오기
    @GetMapping("/events")
    public ResponseEntity<List<CalendarResponseDTO>> getCalendarEvents() {
        List<CalendarResponseDTO> events = calendarService.getCalendarEvents();
        return ResponseEntity.ok(events);
    }
    
    // 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCalendarEntry(@PathVariable("id") Long id, @RequestBody CalendarRequestDTO request) {
        calendarService.updateCalendarEntry(id, request);
        return ResponseEntity.ok("수정 완료");
    }
    
    // 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCalendarEntry(@PathVariable("id") Long id) { 
        calendarService.deleteCalendarEntry(id);
        return ResponseEntity.ok("삭제 완료");
    }
}
