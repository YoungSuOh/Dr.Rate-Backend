package com.bitcamp.drrate.domain.calendar.service;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;

import java.util.List;

public interface CalendarService {
    List<CalendarResponseDTO> getDepositProducts();
    void saveCalendarEntry(CalendarRequestDTO request);
}
