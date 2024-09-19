package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MissionRequest {
    @NotBlank(message = "Mission name is required")
    private String name;

    @NotBlank(message = "Mission description is required")
    private String description;

    @NotNull(message = "Point reward is required")
    @Min(value = 0, message = "Point reward must be non-negative")
    private Integer pointReward;

    @NotNull(message = "Required task count is required")
    @Min(value = 0, message = "Required task count must be non-negative")
    private Integer requiredTaskCount;

    @NotNull(message = "Required duration is required")
    @Min(value = 0, message = "Required duration must be non-negative")
    private Integer requiredDuration;

    @NotNull(message = "Is duration only flag is required")
    private Boolean isDurationOnly;

    @NotNull(message = "Is task only flag is required")
    private Boolean isTaskOnly;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;
}