package com.bitcamp.drrate.domain.calendar.service;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;
import com.bitcamp.drrate.domain.calendar.entity.Calendar;
import com.bitcamp.drrate.domain.calendar.repository.CalendarRepository;
import com.bitcamp.drrate.global.code.resultCode.ErrorStatus; 
import com.bitcamp.drrate.global.exception.exceptionhandler.CalendarServiceExceptionHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    // Calendar 테이블에 접근
    private final CalendarRepository calendarRepository;

    @Override
    public void saveCalendarEntry(CalendarRequestDTO request) {
        try {
            Calendar calendarEntry = Calendar.builder()
                    .cal_user_id(request.getCal_user_id())
                    .installment_name(request.getInstallment_name())
                    .bank_name(request.getBank_name())
                    .amount(request.getAmount())
                    .start_date(request.getStart_date())
                    .end_date(request.getEnd_date())
                    .build();

            calendarRepository.save(calendarEntry);
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_SAVE_FAILED);
        }
    }

    @Override
    public List<CalendarResponseDTO> getCalendarEvents() {
        try {
            return calendarRepository.findAll().stream()
                    .map(entry -> CalendarResponseDTO.builder()
                            .id(entry.getId())
                            .installment_name(entry.getInstallment_name())
                            .bank_name(entry.getBank_name())
                            .start_date(entry.getStart_date())
                            .end_date(entry.getEnd_date())
                            .amount(entry.getAmount())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_QUERY_FAILED);
        }
    }

    @Override
    @Transactional
    public void updateCalendarEntry(Long id, CalendarRequestDTO request) {
        try {
            Calendar calendarEntry = calendarRepository.findById(id)
                    .orElseThrow(() -> new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_EVENT_NOT_FOUND));
            calendarEntry.setInstallment_name(request.getInstallment_name());
            calendarEntry.setBank_name(request.getBank_name());
            calendarEntry.setAmount(request.getAmount());
            calendarEntry.setStart_date(request.getStart_date());
            calendarEntry.setEnd_date(request.getEnd_date());
            calendarRepository.save(calendarEntry);
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteCalendarEntry(Long id) {
        try {
            if (!calendarRepository.existsById(id)) {
                throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_EVENT_NOT_FOUND);
            }
            calendarRepository.deleteById(id);
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_DELETE_FAILED);
        }
    }
}
