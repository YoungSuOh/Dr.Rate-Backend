package com.bitcamp.drrate.domain.calendar.service;

import com.bitcamp.drrate.domain.calendar.dto.request.CalendarRequestDTO;
import com.bitcamp.drrate.domain.calendar.dto.response.CalendarResponseDTO;
import com.bitcamp.drrate.domain.calendar.entity.Calendar;
import com.bitcamp.drrate.domain.calendar.repository.CalendarRepository;
import com.bitcamp.drrate.domain.products.repository.DepositeOptionsRepository;
import com.bitcamp.drrate.domain.products.entity.DepositeOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final DepositeOptionsRepository depositeOptionsRepository;
    private final CalendarRepository calendarRepository;

    @Override
    public List<CalendarResponseDTO> getDepositProducts() {
        List<DepositeOptions> options = depositeOptionsRepository.findAll();
        return options.stream()
                .map(option -> CalendarResponseDTO.builder()
                        .depositName(option.getProducts().getPrdName())
                        .bankName(option.getProducts().getBankName())
                        .interestRate(option.getBasicRate().doubleValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void saveCalendarEntry(CalendarRequestDTO request) {
        Calendar calendarEntry = Calendar.builder()
                .calUserId(request.getCalUserId())
                .calInstallmentId(request.getCalInstallmentId())
                .amount(request.getAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        calendarRepository.save(calendarEntry);
    }
}
