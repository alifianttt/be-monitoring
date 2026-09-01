package com.thtmon.callmonitoring.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_monitoring")
public class CallMonitoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "call_id", nullable = false, length = 50)
    private String callId;

    @Column(name = "call_timestamp", nullable = false)
    private LocalDateTime callTimestamp;

    @Column(name = "cs_name", nullable = false, length = 100)
    private String csName;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "sentiment_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal sentimentScore;

    public CallMonitoring() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public LocalDateTime getCallTimestamp() {
        return callTimestamp;
    }

    public void setCallTimestamp(LocalDateTime callTimestamp) {
        this.callTimestamp = callTimestamp;
    }

    public String getCsName() {
        return csName;
    }

    public void setCsName(String csName) {
        this.csName = csName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(BigDecimal sentimentScore) {
        this.sentimentScore = sentimentScore;
    }
}
