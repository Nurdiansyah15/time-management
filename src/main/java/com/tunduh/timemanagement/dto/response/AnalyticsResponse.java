package com.tunduh.timemanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnalyticsResponse {
    private long totalUsers;
    private long activeUsers;
    private double averageTasksPerUser;
}

