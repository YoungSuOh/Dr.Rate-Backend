package com.bitcamp.drrate.domain.calendar.service;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;
import com.bitcamp.drrate.domain.calendar.entity.Calendar;
import com.bitcamp.drrate.domain.calendar.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    //Calendar 테이블에 접근
    private final CalendarRepository calendarRepository;

    @Override
    public void saveCalendarEntry(CalendarRequestDTO request) {
        Calendar calendarEntry = Calendar.builder()
                .cal_user_id(request.getCal_user_id())
                .installment_name(request.getInstallment_name())
                .bank_name(request.getBank_name())
                .amount(request.getAmount())
                .start_date(request.getStart_date())
                .end_date(request.getEnd_date())
                .build();

        calendarRepository.save(calendarEntry);
    }

    @Override
    public List<CalendarResponseDTO> getCalendarEvents() {
        List<Calendar> calendarEntries = calendarRepository.findAll();
        return calendarEntries.stream()
                .map(entry -> CalendarResponseDTO.builder()
                        .installment_name(entry.getInstallment_name())
                        .bank_name(entry.getBank_name())
                        .start_date(entry.getStart_date())
                        .end_date(entry.getEnd_date())
                        .amount(entry.getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void updateCalendarEntry(Long id, CalendarRequestDTO request) {
        Calendar calendarEntry = calendarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 데이터가 없습니다: " + id));

        calendarEntry.setCal_user_id(request.getCal_user_id());
        calendarEntry.setInstallment_name(request.getInstallment_name());
        calendarEntry.setBank_name(request.getBank_name());
        calendarEntry.setAmount(request.getAmount());
        calendarEntry.setStart_date(request.getStart_date());
        calendarEntry.setEnd_date(request.getEnd_date());

        calendarRepository.save(calendarEntry);
    }

    @Override
    public void deleteCalendarEntry(Long id) {
        calendarRepository.deleteById(id);
    }

	@Override
	public List<CalendarResponseDTO> getInstallmentProducts() {
		// TODO Auto-generated method stub
		return null;
	}
}
