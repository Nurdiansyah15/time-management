package com.tunduh.timemanagement.dto.request;

import com.tunduh.timemanagement.entity.MissionEntity;
import jakarta.persistence.Column;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class AdminMissionRequest {
    @NotBlank(message = "Mission name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Point reward is required")
    @Min(value = 1, message = "Point reward must be at least 1")
    private Integer pointReward;

    @NotNull(message = "Criteria value is required")
    @Min(value = 1, message = "Criteria value must be at least 1")
    private Integer criteriaValue;

    @NotNull(message = "Mission type is required")
    private MissionEntity.Type type;

    @NotNull(message = "Mission icon is required")
    private String missionIcon;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
}