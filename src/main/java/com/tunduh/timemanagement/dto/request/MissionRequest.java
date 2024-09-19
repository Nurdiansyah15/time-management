package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MissionRequest {
    @NotBlank(message = "Mission name is required")
    @Size(min = 1, max = 100, message = "Mission name must be between 1 and 100 characters")
    private String name;

    @NotBlank(message = "Mission description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Submission ID is required")
    private String submissionId;

    @NotNull(message = "Point reward must be filled")
    @Min(value = 0, message = "Point reward must be non-negative")
    @Max(value = 10000, message = "Point reward cannot exceed 10000")
    private Integer pointReward;

    @NotNull(message = "Required task count must be filled")
    @Min(value = 0, message = "Required task count must be non-negative")
    @Max(value = 100, message = "Required task count cannot exceed 100")
    private Integer requiredTaskCount;

    @NotNull(message = "Required duration must be filled")
    @Min(value = 0, message = "Required duration must be non-negative")
    @Max(value = 10080, message = "Required duration cannot exceed one week (10080 minutes)")
    private Integer requiredDuration;

    @NotNull(message = "Duration only flag must be specified")
    private Boolean isDurationOnly;

    @NotNull(message = "Task only flag must be specified")
    private Boolean isTaskOnly;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;
}