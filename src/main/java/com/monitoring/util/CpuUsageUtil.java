package com.monitoring.util;

import com.monitoring.entity.CpuUsage;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample;

@Component
public class CpuUsageUtil {
    private final MetricsEndpoint metricsEndpoint;

    public CpuUsageUtil(MetricsEndpoint metricsEndpoint){
        this.metricsEndpoint = metricsEndpoint;
    }

    public double getCpuUsage(){
        double usage = 0.00;

        List<Sample> measurements = metricsEndpoint.metric("system.cpu.usage", null)
                .getMeasurements();
        for(Sample sample : measurements){
            usage = sample.getValue() * 100;
        }

        // 00.00 (반올림)
        usage = Math.round(usage*100) / 100.0;

        return usage;
    }

    public double calcAvgUsage(List<CpuUsage> list){
        if(list.isEmpty()) return 0.00;

        double sum = 0.00;
        for(CpuUsage cpuUsage : list){
            sum += cpuUsage.getCpuUsage();
        }

        double avg = sum / list.size();

        // 00.00 (반올림)
        return Math.round(avg * 100.0) / 100.0;
    }

    public double calcMinUsage(List<CpuUsage> list){
        if(list.isEmpty()) return 0.0;

        double min = Double.MAX_VALUE;
        for(CpuUsage cpuUsage : list){
            double currentUsage = cpuUsage.getCpuUsage();

            if(currentUsage < min){
                min = currentUsage;
            }
        }

        return min;
    }

    public double calcMaxUsage(List<CpuUsage> list){
        if(list.isEmpty()) return 0.0;

        double max = Double.MIN_VALUE;
        for(CpuUsage cpuUsage : list){
            double currentUsage = cpuUsage.getCpuUsage();

            if(currentUsage > max){
                max = currentUsage;
            }
        }

        return max;
    }
}
