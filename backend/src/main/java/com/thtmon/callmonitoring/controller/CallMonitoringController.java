package com.thtmon.callmonitoring.controller;

import com.thtmon.callmonitoring.dto.CallMonitoringResponseDTO;
import com.thtmon.callmonitoring.dto.PageResponseDTO;
import com.thtmon.callmonitoring.enums.SentimentFilter;
import com.thtmon.callmonitoring.service.CallMonitoringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class CallMonitoringController {

    private final CallMonitoringService service;

    public CallMonitoringController(CallMonitoringService service) {
        this.service = service;
    }

    @GetMapping("/api/call-monitoring")
    public PageResponseDTO<CallMonitoringResponseDTO> getCallMonitoring(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) SentimentFilter sentiment,
            @RequestParam(required = false, defaultValue = "callTimestamp") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.of(endDate, LocalTime.MAX) : null;

        return service.findAll(search, startDateTime, endDateTime, sentiment, sortBy, sortDir, page);
    }
}
