package com.monitoring;

import com.monitoring.entity.CpuUsage;
import com.monitoring.entity.CpuUsageDay;
import com.monitoring.entity.CpuUsageHour;
import com.monitoring.repository.CpuUsageDayRepository;
import com.monitoring.repository.CpuUsageHourRepository;
import com.monitoring.repository.CpuUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class MonitoringApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private CpuUsageRepository cpuUsageRepository;

	@Autowired
	private CpuUsageHourRepository cpuUsageHourRepository;

	@Autowired
	private CpuUsageDayRepository cpuUsageDayRepository;


	@BeforeEach
	void contextLoads() {
		cpuUsageRepository.deleteAll();
		cpuUsageHourRepository.deleteAll();
		cpuUsageDayRepository.deleteAll();

		cpuUsageRepository.saveAll(List.of(
				new CpuUsage(1L, 20.0,
						LocalDateTime.of(2024, 5, 22, 7, 1, 0, 0)),
				new CpuUsage(2L, 30.0,
						LocalDateTime.of(2024, 5, 22, 7, 2, 0, 0))
		));

		cpuUsageHourRepository.saveAll(List.of(
				new CpuUsageHour(1L, 10.0, 30.0, 20.0,
						LocalDate.of(2024, 5, 22), LocalTime.of(7, 0)),
				new CpuUsageHour(2L, 15.0, 25.0, 20.0,
						LocalDate.of(2024, 5, 22), LocalTime.of(8, 0))
		));

		cpuUsageDayRepository.saveAll(List.of(
				new CpuUsageDay(1L, 10.0, 30.0, 20.0,
						LocalDate.of(2024, 5, 21)),
				new CpuUsageDay(2L, 15.0, 25.0, 20.0,
						LocalDate.of(2024, 5, 22))
		));
	}


	@Nested
	@DisplayName("조회API호출")
	class APIGet{

		@Test
		@DisplayName("분단위")
		void minutes() throws Exception {
			//분단위 GET
			mockMvc.perform(get("/api/cpu-usage/minute")
							.param("from", "2024-05-21T00:00:00")
							.param("to", "2024-05-23T00:00:00"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].cpuUsage").value(20.0))
					.andExpect(jsonPath("$[1].cpuUsage").value(30.0));
		}

		@Test
		@DisplayName("시간단위")
		void hours() throws Exception {
			//시간단위 GET
			mockMvc.perform(get("/api/cpu-usage/hour")
							.param("date", "2024-05-22"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].minCpuUsage").value(10.0))
					.andExpect(jsonPath("$[0].maxCpuUsage").value(30.0))
					.andExpect(jsonPath("$[0].avgCpuUsage").value(20.0));
		}

		@Test
		@DisplayName("일단위")
		void days() throws Exception {
			//일단위 GET
			mockMvc.perform(get("/api/cpu-usage/day")
							.param("from", "2024-05-20")
							.param("to", "2024-05-23"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$[0].minCpuUsage").value(10.0))
					.andExpect(jsonPath("$[0].maxCpuUsage").value(30.0))
					.andExpect(jsonPath("$[0].avgCpuUsage").value(20.0));
		}
	}

	@Nested
	@DisplayName("조회API예외")
	class APIException{

		@Test
		@DisplayName("분단위_기간초과")
		void outDateMinutes() throws Exception {
			mockMvc.perform(get("/api/cpu-usage/minute")
							.param("from", "2020-05-21T00:00:00")
							.param("to", "2024-05-23T00:00:00"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("시간단위_기간초과")
		void outDateHours() throws Exception {
			mockMvc.perform(get("/api/cpu-usage/hour")
							.param("date", "2020-05-22"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("일단위_기간초과")
		void outDateDays() throws Exception {
			mockMvc.perform(get("/api/cpu-usage/day")
							.param("from", "2020-05-20")
							.param("to", "2024-05-23"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("값이없는경우")
		void noItemsMinutes() throws Exception {
			mockMvc.perform(get("/api/cpu-usage/minute")
							.param("from", "2024-05-18T00:00:00")
							.param("to", "2024-05-19T00:00:00"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("매개변수가없는경우")
		void noParameters() throws Exception {
			mockMvc.perform(get("/api/cpu-usage/minute"))
					.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("올바르지않은형식")
		void typeMismatch() throws Exception {
			mockMvc.perform(get("/api/cpu-usage/minute")
							.param("from", "202A-05-18T00:00:00")
							.param("to", "202A-05-19T00:00:00"))
					.andExpect(status().isBadRequest());
		}
	}
	

}
