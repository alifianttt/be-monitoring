package com.thtmon.callmonitoring.dto;

import com.thtmon.callmonitoring.entity.CallMonitoring;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CallMonitoringResponseDTO {

    private Long id;
    private String callId;
    private LocalDateTime callTimestamp;
    private String csName;
    private String customerName;
    private BigDecimal sentimentScore;

    public static CallMonitoringResponseDTO fromEntity(CallMonitoring entity) {
        CallMonitoringResponseDTO dto = new CallMonitoringResponseDTO();
        dto.id = entity.getId();
        dto.callId = entity.getCallId();
        dto.callTimestamp = entity.getCallTimestamp();
        dto.csName = entity.getCsName();
        dto.customerName = entity.getCustomerName();
        dto.sentimentScore = entity.getSentimentScore();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getCallId() {
        return callId;
    }

    public LocalDateTime getCallTimestamp() {
        return callTimestamp;
    }

    public String getCsName() {
        return csName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getSentimentScore() {
        return sentimentScore;
    }
}
