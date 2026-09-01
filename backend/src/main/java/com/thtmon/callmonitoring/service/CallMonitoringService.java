package com.thtmon.callmonitoring.service;

import com.thtmon.callmonitoring.dto.CallMonitoringResponseDTO;
import com.thtmon.callmonitoring.dto.PageResponseDTO;
import com.thtmon.callmonitoring.entity.CallMonitoring;
import com.thtmon.callmonitoring.enums.SentimentFilter;
import com.thtmon.callmonitoring.repository.CallMonitoringRepository;
import com.thtmon.callmonitoring.repository.spec.CallMonitoringSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
public class CallMonitoringService {

    private static final int PAGE_SIZE = 5;

    // Only these columns are sortable, and only these are exposed to the API,
    // to avoid letting a client sort by an arbitrary/internal entity field.
    private static final Map<String, String> SORTABLE_COLUMNS = Map.of(
            "callId", "callId",
            "callTimestamp", "callTimestamp",
            "csName", "csName",
            "customerName", "customerName",
            "sentimentScore", "sentimentScore"
    );

    private static final Set<String> ASC_DIRECTIONS = Set.of("asc", "ASC");

    private final CallMonitoringRepository repository;

    public CallMonitoringService(CallMonitoringRepository repository) {
        this.repository = repository;
    }

    public PageResponseDTO<CallMonitoringResponseDTO> findAll(
            String search,
            LocalDateTime startDate,
            LocalDateTime endDate,
            SentimentFilter sentimentFilter,
            String sortBy,
            String sortDir,
            int page
    ) {
        Specification<CallMonitoring> spec = Specification.where(CallMonitoringSpecifications.search(search))
                .and(CallMonitoringSpecifications.periodBetween(startDate, endDate))
                .and(CallMonitoringSpecifications.sentiment(sentimentFilter));

        String property = SORTABLE_COLUMNS.getOrDefault(sortBy, "callTimestamp");
        Sort.Direction direction = ASC_DIRECTIONS.contains(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;

        int safePage = Math.max(page, 0);
        PageRequest pageRequest = PageRequest.of(safePage, PAGE_SIZE, Sort.by(direction, property));

        Page<CallMonitoring> result = repository.findAll(spec, pageRequest);
        Page<CallMonitoringResponseDTO> mapped = result.map(CallMonitoringResponseDTO::fromEntity);

        return PageResponseDTO.from(mapped);
    }
}
