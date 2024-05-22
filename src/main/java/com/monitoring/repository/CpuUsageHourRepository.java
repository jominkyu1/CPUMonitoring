package com.monitoring.repository;

import com.monitoring.entity.CpuUsageHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CpuUsageHourRepository extends JpaRepository<CpuUsageHour, Long> {
    List<CpuUsageHour> findByDay(LocalDate day);
}
