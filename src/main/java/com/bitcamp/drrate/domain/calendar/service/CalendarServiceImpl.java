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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository calendarRepository;

    // 입력(저장)
    @Override
    @Transactional
    public void saveCalendarEntries(List<CalendarRequestDTO> requests) {
        try {
        	// 그룹 ID 생성
            String groupId = UUID.randomUUID().toString();

            List<Calendar> calendarEntries = requests.stream().map(request -> Calendar.builder()
                    .cal_user_id(request.getCal_user_id())
                    .bank_name(request.getBank_name())
                    .installment_name(request.getInstallment_name())
                    .amount(request.getAmount())
                    .start_date(request.getStart_date())
                    .end_date(request.getEnd_date())
                    .groupId(groupId)
                    .build())
                .collect(Collectors.toList());

            calendarRepository.saveAll(calendarEntries); // DB 저장
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_SAVE_FAILED);
        }
    }

    // 목록
    @Override
    public List<CalendarResponseDTO> getCalendarEvents(Long userId) {
        try {
            // 그룹별 최초 시작일 가져오기
            List<Object[]> groupStartDates = calendarRepository.findGroupStartDates();
            Map<String, LocalDate> groupStartDateMap = groupStartDates.stream()
                .collect(Collectors.toMap(
                    row -> (String) row[0], // 그룹ID
                    row -> (LocalDate) row[1] // 시작날짜(고정)
                ));

            // 사용자별 이벤트 가져오기
            return calendarRepository.findByCalUserId(userId).stream()
                .map(entry -> CalendarResponseDTO.builder()
                    .id(entry.getId()) // 이벤트 ID
                    .bank_name(entry.getBank_name())
                    .installment_name(entry.getInstallment_name())
                    .start_date(entry.getStart_date())
                    .end_date(entry.getEnd_date())
                    .amount(entry.getAmount())
                    .fixedStartDate(groupStartDateMap.get(entry.getGroupId())) // 최초 시작일 추가
                    .build())
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_QUERY_FAILED);
        }
    }

    // 수정
    @Override
    @Transactional
    public void updateCalendarGroup(Long id, Long userId, CalendarRequestDTO request) {
        try {
            Calendar calendarEntry = calendarRepository.findById(id)
                    .orElseThrow(() -> new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_EVENT_NOT_FOUND));
            
            // 사용자 ID 일치여부
            if (!calendarEntry.getCal_user_id().equals(userId)) {
                throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_EVENT_NOT_FOUND);
            }

            calendarRepository.updateByGroupId(
                calendarEntry.getGroupId(),
                request.getBank_name(),
                request.getInstallment_name(),
                request.getAmount(),
                request.getEnd_date()
            );
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_UPDATE_FAILED);
        }
    }

    // 삭제
    @Override
    @Transactional
    public void deleteCalendarGroup(Long id, Long userId) {
        try {
            Calendar calendarEntry = calendarRepository.findById(id)
                    .orElseThrow(() -> new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_EVENT_NOT_FOUND));

            if (!calendarEntry.getCal_user_id().equals(userId)) {
                throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_EVENT_NOT_FOUND);
            }

            calendarRepository.deleteByGroupId(calendarEntry.getGroupId());
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_DELETE_FAILED);
        }
    }

    
    
    // ---- Products 데이터 관련 ----
    // 은행명, 로고
    @Override
    public List<Map<String, String>> getDistinctBankNamesAndLogos() {
        try {
            List<Object[]> results = calendarRepository.findDistinctBankNamesAndLogos();
            List<Map<String, String>> bankList = new ArrayList<>();

            for (Object[] result : results) {
                Map<String, String> bank = new HashMap<>();
                bank.put("bankName", (String) result[0]); // 은행명
                bank.put("bankLogo", (String) result[1]); // 은행 로고
                bankList.add(bank);
            }

            return bankList;
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_QUERY_FAILED);
        }
    }
    
    // 특정 은행의 적금명목록
    @Override
    public List<String> getProductNamesByBankName(String bankName) {
        try {
            return calendarRepository.findProductNamesByBankName(bankName);
        } catch (Exception e) {
            throw new CalendarServiceExceptionHandler(ErrorStatus.CALENDAR_QUERY_FAILED);
        }
    }
}

