package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;
import com.tunduh.timemanagement.dto.response.BudgetAnalyticsResponse;
import com.tunduh.timemanagement.dto.response.DailyTaskAnalyticsResponse;

import java.time.LocalDate;

public interface UserAnalyticsService {
    UserAnalyticsResponse getUserAnalytics(String userId);
    UserAnalyticsResponse getTaskCompletionAnalytics(String userId, String period);
    UserAnalyticsResponse getCompletedMissionsAnalytics(String userId);
    BudgetAnalyticsResponse getBudgetAnalytics(String userId);
    DailyTaskAnalyticsResponse getDailyTaskAnalytics(String userId, LocalDate date);
}