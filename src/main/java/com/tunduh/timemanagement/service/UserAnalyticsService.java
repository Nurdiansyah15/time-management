package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import java.time.LocalDate;

public interface UserAnalyticsService {
    UserAnalyticsResponse getUserAnalyticsDashboard(String userId);
    UserAnalyticsResponse getTaskAnalytics(String userId, LocalDate startDate, LocalDate endDate);
    UserAnalyticsResponse getMissionAnalytics(String userId);
    UserAnalyticsResponse getBudgetAnalytics(String userId, LocalDate startDate, LocalDate endDate);
    String getTaskAnalyticsCSV(String userId, LocalDate startDate, LocalDate endDate);
    String getMissionAnalyticsCSV(String userId);
    String getBudgetAnalyticsCSV(String userId, LocalDate startDate, LocalDate endDate);
}