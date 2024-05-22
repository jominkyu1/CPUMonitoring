package com.monitoring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class CpuUsageHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double minCpuUsage;
    private double maxCpuUsage;
    private double avgCpuUsage;

    private LocalDate day;
    private LocalTime time;

    public CpuUsageHour() {

    }
}
