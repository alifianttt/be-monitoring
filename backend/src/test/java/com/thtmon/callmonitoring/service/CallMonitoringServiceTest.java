package com.thtmon.callmonitoring.service;

import com.thtmon.callmonitoring.dto.PageResponseDTO;
import com.thtmon.callmonitoring.entity.CallMonitoring;
import com.thtmon.callmonitoring.enums.SentimentFilter;
import com.thtmon.callmonitoring.repository.CallMonitoringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
class CallMonitoringServiceTest {

    @Autowired
    private CallMonitoringService service;

    @Autowired
    private CallMonitoringRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        repository.save(row("CALL-0001", "2026-08-01T10:00:00", "Andi", "Rina", "60.00"));
        repository.save(row("CALL-0002", "2026-08-05T11:00:00", "Budi", "Joko", "85.50"));
        repository.save(row("CALL-0003", "2026-07-20T09:30:00", "Citra", "Siti", "45.00"));
    }

    private CallMonitoring row(String callId, String timestamp, String cs, String customer, String score) {
        CallMonitoring entity = new CallMonitoring();
        entity.setCallId(callId);
        entity.setCallTimestamp(LocalDateTime.parse(timestamp));
        entity.setCsName(cs);
        entity.setCustomerName(customer);
        entity.setSentimentScore(new BigDecimal(score));
        return entity;
    }

    @Test
    void filtersRecordsBelow70Percent() {
        PageResponseDTO<?> result = service.findAll(
                null, null, null, SentimentFilter.BELOW_70, "callTimestamp", "desc", 0);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtersRecordsAtOrAbove70Percent() {
        PageResponseDTO<?> result = service.findAll(
                null, null, null, SentimentFilter.AT_OR_ABOVE_70, "callTimestamp", "desc", 0);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchMatchesCsNameAcrossColumns() {
        PageResponseDTO<?> result = service.findAll(
                "Budi", null, null, null, "callTimestamp", "desc", 0);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void periodFilterIsInclusiveOfBoundaryDates() {
        PageResponseDTO<?> result = service.findAll(
                null,
                LocalDateTime.parse("2026-08-01T00:00:00"),
                LocalDateTime.parse("2026-08-05T23:59:59"),
                null, "callTimestamp", "desc", 0);

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void combinesSearchPeriodAndSentimentFiltersTogether() {
        PageResponseDTO<?> result = service.findAll(
                "Andi",
                LocalDateTime.parse("2026-08-01T00:00:00"),
                LocalDateTime.parse("2026-08-05T23:59:59"),
                SentimentFilter.BELOW_70,
                "callTimestamp", "desc", 0);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
