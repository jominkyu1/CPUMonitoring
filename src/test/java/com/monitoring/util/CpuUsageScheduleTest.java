package com.monitoring.util;

import com.monitoring.entity.CpuUsage;
import com.monitoring.entity.CpuUsageDay;
import com.monitoring.entity.CpuUsageHour;
import com.monitoring.repository.CpuUsageDayRepository;
import com.monitoring.repository.CpuUsageHourRepository;
import com.monitoring.repository.CpuUsageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 스케줄 INSERT 단위 기능 테스트
 * */
@ExtendWith(MockitoExtension.class)
class CpuUsageScheduleTest {
    @InjectMocks
    private CpuUsageSchedule cpuUsageSchedule;

    @Mock
    private CpuUsageUtil cpuUsageUtil;

    @Mock
    private CpuUsageRepository cpuUsageRepository;

    @Mock
    private CpuUsageHourRepository cpuUsageHourRepository;

    @Mock
    private CpuUsageDayRepository cpuUsageDayRepository;

    @Test
    void everyMinuteInsertTest() {
        double cpuUsage = 10.10;
        when(cpuUsageUtil.getCpuUsage()).thenReturn(cpuUsage);

        //when
        cpuUsageSchedule.insertCpuUsage();

        //then
        ArgumentCaptor<CpuUsage> captor = ArgumentCaptor.forClass(CpuUsage.class);
        verify(cpuUsageRepository).save(captor.capture());
        CpuUsage savedCpuUsage = captor.getValue();

        assertEquals(cpuUsage, savedCpuUsage.getCpuUsage(), 0.01);
    }

    @Test
    void everyHourInsertTest() {
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime oneHourMinus = now.minusHours(1);
        List<CpuUsage> cpuUsageList = new ArrayList<>();

        when(cpuUsageRepository.findByTimestampBetween(oneHourMinus, now)).thenReturn(cpuUsageList);
        when(cpuUsageUtil.calcMinUsage(cpuUsageList)).thenReturn(1.50);
        when(cpuUsageUtil.calcMaxUsage(cpuUsageList)).thenReturn(10.50);
        when(cpuUsageUtil.calcAvgUsage(cpuUsageList)).thenReturn(6.00);

        //when
        cpuUsageSchedule.insertCpuUsageHour();

        //then
        ArgumentCaptor<CpuUsageHour> captor = ArgumentCaptor.forClass(CpuUsageHour.class);
        verify(cpuUsageHourRepository).save(captor.capture());

        CpuUsageHour capturedValue = captor.getValue();
        assertEquals(1.50, capturedValue.getMinCpuUsage(), 0.01);
        assertEquals(10.50, capturedValue.getMaxCpuUsage(), 0.01);
        assertEquals(6.00, capturedValue.getAvgCpuUsage(), 0.01);
    }

    @Test
    void everyDayInsertTest() {
        LocalDate now = LocalDate.now();
        LocalDate oneDayMinus = now.minusDays(1);
        List<CpuUsage> cpuUsageList = new ArrayList<>();

        when(cpuUsageRepository.findByTimestampBetween(oneDayMinus.atStartOfDay(), oneDayMinus.atTime(LocalTime.MAX))).thenReturn(cpuUsageList);
        when(cpuUsageUtil.calcMinUsage(cpuUsageList)).thenReturn(10.0);
        when(cpuUsageUtil.calcMaxUsage(cpuUsageList)).thenReturn(90.0);
        when(cpuUsageUtil.calcAvgUsage(cpuUsageList)).thenReturn(50.0);

        //when
        cpuUsageSchedule.insertCpuUsageDay();

        //then
        ArgumentCaptor<CpuUsageDay> captor = ArgumentCaptor.forClass(CpuUsageDay.class);
        verify(cpuUsageDayRepository).save(captor.capture());

        CpuUsageDay capturedCpuUsageDay = captor.getValue();
        assertEquals(10.0, capturedCpuUsageDay.getMinCpuUsage(), 0.01);
        assertEquals(90.0, capturedCpuUsageDay.getMaxCpuUsage(), 0.01);
        assertEquals(50.0, capturedCpuUsageDay.getAvgCpuUsage(), 0.01);
        assertEquals(oneDayMinus, capturedCpuUsageDay.getDay());
    }
}