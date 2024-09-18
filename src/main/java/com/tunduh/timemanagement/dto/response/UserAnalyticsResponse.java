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
    private double totalPointsChange;
    private Map<String, Long> taskCompletionByDate;
    private Map<String, Long> taskStatusCounts;
    private Map<String, Long> missionStatusCounts;
    private Map<String, Double> pointsChangeByCategory;
}