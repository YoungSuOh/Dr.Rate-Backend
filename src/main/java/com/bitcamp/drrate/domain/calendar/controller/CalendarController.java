package com.bitcamp.drrate.domain.calendar.controller;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;
import com.bitcamp.drrate.domain.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    //예금 목록 가져오기
    @GetMapping("/deposits")
    public List<CalendarResponseDTO> getDeposits() {
        return calendarService.getDepositProducts();
    }

    //달력 이벤트 저장
    @PostMapping("/save")
    public void saveCalendarEntry(@RequestBody CalendarRequestDTO request) {
        calendarService.saveCalendarEntry(request);
    }
}
