package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.AnalyticsDataOverallResponse;
import com.tunduh.timemanagement.dto.response.AnalyticsDataResponse;
import com.tunduh.timemanagement.dto.response.DailyStats;
import com.tunduh.timemanagement.dto.response.OverallStats;
import com.tunduh.timemanagement.repository.AnalyticsRepository;
import com.tunduh.timemanagement.service.AnalyticsService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final AnalyticsRepository analyticsRepository;

        @Override
        public AnalyticsDataResponse getAnalytics(String userId, LocalDate startDate, LocalDate endDate) {
            if (startDate.plusDays(7).isBefore(endDate)) {
                endDate = startDate.plusDays(6);
            }

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            List<Object[]> dailyData = analyticsRepository.getAnalyticsData(userId, startDateTime, endDateTime);

            Map<LocalDate, DailyStats> dailyStatsMap = dailyData.stream()
                    .collect(Collectors.toMap(
                            data -> ((java.sql.Date) data[0]).toLocalDate(),
                            data -> DailyStats.builder()
                                    .date(((java.sql.Date) data[0]).toLocalDate())
                                    .taskCount(((Number) data[1]).intValue())
                                    .duration(((Number) data[2]).longValue())
                                    .energy(((Number) data[3]).intValue())
                                    .build()
                    ));

            List<DailyStats> dailyStats = new ArrayList<>();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                dailyStats.add(dailyStatsMap.getOrDefault(date, DailyStats.builder()
                        .date(date)
                        .taskCount(0)
                        .duration(0)
                        .energy(0)
                        .build()));
            }

            return AnalyticsDataResponse.builder()
                    .dailyStats(dailyStats)
                    .build();
        }

    @Override
    public OverallStats getOverallStats(String userId) {
        return analyticsRepository.getOverallStats(userId);
    }
}
