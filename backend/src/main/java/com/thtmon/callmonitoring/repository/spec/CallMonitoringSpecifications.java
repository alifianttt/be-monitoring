package com.thtmon.callmonitoring.repository.spec;

import com.thtmon.callmonitoring.entity.CallMonitoring;
import com.thtmon.callmonitoring.enums.SentimentFilter;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * All filters here are composed into a single WHERE clause by the repository,
 * so search + period + sentiment always combine (per the user story AC),
 * instead of being applied in three separate in-memory passes.
 */
public final class CallMonitoringSpecifications {

    private CallMonitoringSpecifications() {
    }

    public static Specification<CallMonitoring> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String like = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("callId")), like),
                cb.like(cb.lower(root.get("csName")), like),
                cb.like(cb.lower(root.get("customerName")), like),
                cb.like(cb.function("to_char", String.class, root.get("callTimestamp"),
                        cb.literal("YYYY-MM-DD HH24:MI")), like),
                cb.like(cb.function("to_char", String.class, root.get("sentimentScore"),
                        cb.literal("FM990.00")), like)
        );
    }

    public static Specification<CallMonitoring> periodBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) {
            return null;
        }
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("callTimestamp"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("callTimestamp"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("callTimestamp"), end);
            }
        };
    }

    public static Specification<CallMonitoring> sentiment(SentimentFilter filter) {
        if (filter == null) {
            return null;
        }
        BigDecimal threshold = new BigDecimal("70");
        if (filter == SentimentFilter.BELOW_70) {
            return (root, query, cb) -> cb.lessThan(root.get("sentimentScore"), threshold);
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("sentimentScore"), threshold);
    }
}
