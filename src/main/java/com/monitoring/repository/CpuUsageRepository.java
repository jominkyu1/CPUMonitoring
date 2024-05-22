package com.monitoring.repository;

import com.monitoring.entity.CpuUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CpuUsageRepository extends JpaRepository<CpuUsage, Long> {
    List<CpuUsage> findByTimestampBetween(LocalDateTime from, LocalDateTime to);
}
