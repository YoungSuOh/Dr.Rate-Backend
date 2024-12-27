package com.bitcamp.drrate.domain.calendar.service;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;

import java.util.List;
import java.util.Map;

public interface CalendarService {
	void saveCalendarEntries(List<CalendarRequestDTO> requests); // 이벤트 저장
    
    List<CalendarResponseDTO> getCalendarEvents(Long userId); // 목록 조회
    
    void updateCalendarGroup(Long id, Long userId, CalendarRequestDTO request); // 그룹 수정
    
    void deleteCalendarGroup(Long id, Long userId); // 그룹 삭제

	List<Map<String, String>> getDistinctBankNamesAndLogos(); // 은행명, 로고

	List<String> getProductNamesByBankName(String bankName); // 적금명 
}