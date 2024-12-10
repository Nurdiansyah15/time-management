package com.tunduh.timemanagement.dto.response;

import com.tunduh.timemanagement.entity.MissionEntity;
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
    private Integer criteriaValue;
    private Integer criteriaCompleted;
    private String missionIcon;
    private MissionEntity.Type type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private MissionEntity.MissionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}