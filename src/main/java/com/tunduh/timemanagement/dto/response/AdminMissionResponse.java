package com.tunduh.timemanagement.dto.response;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminMissionResponse {
    private String id;
    private String name;
    private String description;
    private Integer pointReward;
    private Integer requiredTaskCount;
    private Integer requiredDuration;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}