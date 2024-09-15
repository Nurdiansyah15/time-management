package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UserAnalyticsResponse {
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long completedMissions;
    private double totalSpent;
    private Map<String, Long> taskCompletionData;
    private Map<String, Long> completedMissionsData;
    private Map<String, Double> spendingByCategory;
}