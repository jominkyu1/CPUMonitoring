package com.monitoring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class CpuUsageDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double minCpuUsage;
    private double maxCpuUsage;
    private double avgCpuUsage;

    private LocalDate day;

    public CpuUsageDay() {

    }
}
