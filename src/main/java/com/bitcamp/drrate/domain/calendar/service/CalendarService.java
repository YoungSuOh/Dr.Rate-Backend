package com.bitcamp.drrate.domain.calendar.service;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;

import java.util.List;

public interface CalendarService {
    List<CalendarResponseDTO> getInstallmentProducts(); // 적금 상품 목록 조회
    
    void saveCalendarEntry(CalendarRequestDTO request); // 이벤트 저장
    
	List<CalendarResponseDTO> getCalendarEvents(); // 이벤트 목록 조회
	
	void updateCalendarEntry(Long id, CalendarRequestDTO request); //이벤트 수정
	
	void deleteCalendarEntry(Long id); //이벤트 삭제
}
