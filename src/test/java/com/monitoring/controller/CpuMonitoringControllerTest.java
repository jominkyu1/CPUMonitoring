package com.monitoring.controller;

import com.monitoring.entity.CpuUsage;
import com.monitoring.entity.CpuUsageDay;
import com.monitoring.entity.CpuUsageHour;
import com.monitoring.service.CpuUsageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 컨트롤러 API 조회 기능 테스트
 * */
@WebMvcTest(CpuMonitoringController.class)
class CpuMonitoringControllerTest {
    @MockBean
    private CpuUsageService cpuUsageService;

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void getCpuUsageEveryMinute() throws Exception{
        List<CpuUsage> cpuUsageList = new ArrayList<>();
        cpuUsageList.add(new CpuUsage(1L, 5.00,
                LocalDateTime.of(2024, 5, 22, 07, 30, 0, 0)));

        when(cpuUsageService.getCpuUsageMinuteBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(cpuUsageList);

        mockMvc.perform(get("/api/cpu-usage/minute")
                .param("from", "2024-05-22T00:00:00")
                .param("to", "2024-05-22T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cpuUsage").value(5.00));

    }

    @Test
    void getCpuUsageEveryHour() throws Exception {
        List<CpuUsageHour> cpuUsageList = new ArrayList<>();
        cpuUsageList.add(new CpuUsageHour(1L, 10.0, 30.0, 20.0,
                LocalDate.of(2024, 5, 22), LocalTime.now()));

        when(cpuUsageService.getCpuUsageHour(any(LocalDate.class)))
                .thenReturn(cpuUsageList);

        mockMvc.perform(get("/api/cpu-usage/hour")
                .param("date", "2024-05-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].minCpuUsage").value(10.0))
                .andExpect(jsonPath("$[0].maxCpuUsage").value(30.0))
                .andExpect(jsonPath("$[0].avgCpuUsage").value(20.0));
    }

    @Test
    void getCpuUsageEveryDay() throws Exception{
        List<CpuUsageDay> cpuUsageList = new ArrayList<>();
        cpuUsageList.add(new CpuUsageDay(1L, 10.0, 30.0, 20.0,
                LocalDate.of(2024, 5, 22)));

        when(cpuUsageService.getCpuUsageDay(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(cpuUsageList);

        mockMvc.perform(get("/api/cpu-usage/day")
                .param("from", "2024-05-21")
                .param("to", "2024-05-23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].minCpuUsage").value(10.0))
                .andExpect(jsonPath("$[0].maxCpuUsage").value(30.0))
                .andExpect(jsonPath("$[0].avgCpuUsage").value(20.0));
    }
}