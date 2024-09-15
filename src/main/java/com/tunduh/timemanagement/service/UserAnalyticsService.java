package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.UserAnalyticsResponse;

public interface UserAnalyticsService {
    UserAnalyticsResponse getUserAnalytics(String userId);
    UserAnalyticsResponse getTaskCompletionAnalytics(String userId, String period);
    UserAnalyticsResponse getCompletedMissionsAnalytics(String userId);
    UserAnalyticsResponse getBudgetAnalytics(String userId);
}