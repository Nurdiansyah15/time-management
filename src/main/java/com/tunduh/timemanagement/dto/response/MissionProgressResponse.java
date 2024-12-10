package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionProgressResponse {
    private String missionId;
    private String missionName;
    private int completedTasks;
    private int totalRequiredTasks;
    private long completedDuration;
    private long totalRequiredDuration;
    private double progressPercentage;
}