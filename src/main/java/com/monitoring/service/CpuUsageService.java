package com.monitoring.service;

import com.monitoring.entity.CpuUsage;
import com.monitoring.entity.CpuUsageDay;
import com.monitoring.entity.CpuUsageHour;
import com.monitoring.repository.CpuUsageDayRepository;
import com.monitoring.repository.CpuUsageHourRepository;
import com.monitoring.repository.CpuUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CpuUsageService {
    private final CpuUsageRepository cpuUsageRepository;
    private final CpuUsageHourRepository cpuUsageHourRepository;
    private final CpuUsageDayRepository cpuUsageDayRepository;

    //분 단위 조회
    public List<CpuUsage> getCpuUsageMinuteBetween(LocalDateTime from, LocalDateTime to){
        return cpuUsageRepository.findByTimestampBetween(from, to);
    }

    //시 단위 조회
    public List<CpuUsageHour> getCpuUsageHour(LocalDate day){
        return cpuUsageHourRepository.findByDay(day);
    }

    //일 단위 조회
    public List<CpuUsageDay> getCpuUsageDay(LocalDate from, LocalDate to){
        return cpuUsageDayRepository.findByDayBetween(from, to);
    }
}
