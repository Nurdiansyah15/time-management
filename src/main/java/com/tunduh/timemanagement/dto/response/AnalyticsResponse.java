package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalyticsResponse {
    private long totalUsers;
    private long activeUsers;
    private double averageTasksPerUser;
}