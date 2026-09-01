package com.thtmon.callmonitoring.repository;

import com.thtmon.callmonitoring.entity.CallMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CallMonitoringRepository
        extends JpaRepository<CallMonitoring, Long>, JpaSpecificationExecutor<CallMonitoring> {
}
