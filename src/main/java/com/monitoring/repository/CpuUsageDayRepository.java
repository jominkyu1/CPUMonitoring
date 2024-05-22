package com.monitoring.repository;

import com.monitoring.entity.CpuUsageDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CpuUsageDayRepository extends JpaRepository<CpuUsageDay, Long> {
    List<CpuUsageDay> findByDayBetween(LocalDate from, LocalDate to);
}
