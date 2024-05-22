package com.monitoring.controller;

import com.monitoring.entity.CpuUsage;
import com.monitoring.entity.CpuUsageDay;
import com.monitoring.entity.CpuUsageHour;
import com.monitoring.service.CpuUsageService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;


@Api(tags = "CPU 사용량 조회 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CpuMonitoringController {

    private final CpuUsageService cpuUsageService;

    @Operation(summary = "분 단위 CPU사용률 조회",
               description = "데이터 제공 기한: 최근 1주")
    @GetMapping("/api/cpu-usage/minute")
    public ResponseEntity<?> getCpuUsageForLastWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to){

            //데이터 제공기한 최근 일주일까지
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
            if(from.isBefore(oneWeekAgo)) throw new IllegalArgumentException();

            List<CpuUsage> cpuUsageList = cpuUsageService.getCpuUsageMinuteBetween(from, to);
            if(cpuUsageList.isEmpty()) throw new NoSuchElementException();

            return ResponseEntity.ok(cpuUsageList);
    }

    @Operation(summary = "시 단위 CPU사용률 조회",
            description = "데이터 제공 기한: 최근 3달")
    @GetMapping("/api/cpu-usage/hour")
    public ResponseEntity<?> getCpuUsageForLastThreeMonths(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        //데이터 제공기한 최근 3개월까지
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        if(date.isBefore(threeMonthsAgo)) throw new IllegalArgumentException();

        List<CpuUsageHour> cpuUsageHourList = cpuUsageService.getCpuUsageHour(date);
        if(cpuUsageHourList.isEmpty()) throw new NoSuchElementException();

        return ResponseEntity.ok(cpuUsageHourList);
    }
    
    @Operation(summary = "일 단위 CPU사용률 조회",
            description = "데이터 제공 기한: 최근 1년")
    @GetMapping("/api/cpu-usage/day")
    public ResponseEntity<?> getCpuUsageForLastYear(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){

        //데이터 제공기한 최근 1년까지
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        if(from.isBefore(oneYearAgo)) throw new IllegalArgumentException();

        List<CpuUsageDay> cpuUsageDayList = cpuUsageService.getCpuUsageDay(from, to);
        if(cpuUsageDayList.isEmpty()) throw new NoSuchElementException();

        return ResponseEntity.ok(cpuUsageDayList);
    }
}
