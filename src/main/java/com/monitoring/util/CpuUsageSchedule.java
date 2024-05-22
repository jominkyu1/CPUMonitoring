package com.monitoring.util;

import com.monitoring.entity.CpuUsage;
import com.monitoring.entity.CpuUsageDay;
import com.monitoring.entity.CpuUsageHour;
import com.monitoring.repository.CpuUsageDayRepository;
import com.monitoring.repository.CpuUsageHourRepository;
import com.monitoring.repository.CpuUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CpuUsageSchedule {
    private final CpuUsageUtil cpuUsageUtil;
    private final CpuUsageRepository cpuUsageRepository;
    private final CpuUsageHourRepository cpuUsageHourRepository;
    private final CpuUsageDayRepository cpuUsageDayRepository;

    //cron = 초 분 시간 일 월(1-12) 요일(0-6)
    @Scheduled(cron = "0 * * * * *") //매분마다
    public void insertCpuUsage() {
        double cpuUsage = cpuUsageUtil.getCpuUsage();

        CpuUsage cpuUsageEntity = new CpuUsage();
        cpuUsageEntity.setCpuUsage(cpuUsage);
        cpuUsageEntity.setTimestamp(LocalDateTime.now().withSecond(0).withNano(0));

        cpuUsageRepository.save(cpuUsageEntity);
        log.info("Every minute schedule");
    }
    
    @Scheduled(cron = "0 0 * * * *") //매시간
    public void insertCpuUsageHour(){
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime oneHourMinus = now.minusHours(1);
        List<CpuUsage> list = cpuUsageRepository.findByTimestampBetween(oneHourMinus, now);

        CpuUsageHour cpuUsageHourEntity = new CpuUsageHour();

        cpuUsageHourEntity.setMinCpuUsage(cpuUsageUtil.calcMinUsage(list));
        cpuUsageHourEntity.setMaxCpuUsage(cpuUsageUtil.calcMaxUsage(list));
        cpuUsageHourEntity.setAvgCpuUsage(cpuUsageUtil.calcAvgUsage(list));
        cpuUsageHourEntity.setDay(oneHourMinus.toLocalDate());
        cpuUsageHourEntity.setTime(oneHourMinus.withMinute(0).withSecond(0).withNano(0).toLocalTime());

        cpuUsageHourRepository.save(cpuUsageHourEntity);
        log.info("Every hour schedule");
    }

    @Scheduled(cron = "0 0 0 * * *") //매일
    public void insertCpuUsageDay(){
        LocalDate now = LocalDate.now();
        LocalDate oneDayMinus = now.minusDays(1);
        List<CpuUsage> list =

                //전일 00:00:00 ~ 23:59:59
                cpuUsageRepository.findByTimestampBetween(
                        oneDayMinus.atStartOfDay(),
                        oneDayMinus.atTime(LocalTime.MAX)
                );

        CpuUsageDay cpuUsageDayEntity = new CpuUsageDay();

        cpuUsageDayEntity.setMinCpuUsage(cpuUsageUtil.calcMinUsage(list));
        cpuUsageDayEntity.setMaxCpuUsage(cpuUsageUtil.calcMaxUsage(list));
        cpuUsageDayEntity.setAvgCpuUsage(cpuUsageUtil.calcAvgUsage(list));
        cpuUsageDayEntity.setDay(oneDayMinus);

        cpuUsageDayRepository.save(cpuUsageDayEntity);
        log.info("Every day schedule");
    }
}
