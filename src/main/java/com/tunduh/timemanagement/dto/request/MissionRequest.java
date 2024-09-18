package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MissionRequest {
    @NotBlank(message = "Mission name is required")
    private String name;

    @NotNull(message = "Point reward must be filled")
    @Min(value = 0, message = "Point reward must be non-negative")
    private Integer pointReward;

    @NotNull(message = "Required task count must be filled")
    @Min(value = 0, message = "Required task count must be non-negative")
    private Integer requiredTaskCount;

    @NotNull(message = "Required duration must be filled")
    @Min(value = 0, message = "Required duration must be non-negative")
    private Integer requiredDuration;

    private String parentMissionId;
}