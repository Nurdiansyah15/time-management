package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.AnalyticsDataResponse;
import com.tunduh.timemanagement.dto.response.OverallStats;

import java.time.LocalDate;

public interface AnalyticsService {
    AnalyticsDataResponse getAnalytics(String userId, LocalDate startDate, LocalDate endDate);
    OverallStats getOverallStats(String userId);
}
