package com.tunduh.timemanagement.dto.request;

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

    @NotNull(message = "Required task count is required")
    @Min(value = 0, message = "Required task count must be non-negative")
    private Integer requiredTaskCount;

    @NotNull(message = "Required duration is required")
    @Min(value = 0, message = "Required duration must be non-negative")
    private Integer requiredDuration;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
}
