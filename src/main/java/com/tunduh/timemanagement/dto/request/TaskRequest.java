package com.tunduh.timemanagement.dto.request;

import com.tunduh.timemanagement.validation.EnumValidator;
import jakarta.validation.constraints.*;
import lombok.Data;

enum TaskStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}

enum TaskPriority {
    LOW, MEDIUM, HIGH, URGENT
}

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotNull(message = "Energy is required")
    @Min(value = 1, message = "Energy must be at least 1")
    @Max(value = 100, message = "Energy cannot exceed 100")
    private Integer energy;

    @Pattern(regexp = "^(DAILY|WEEKLY|MONTHLY|NONE)$", message = "Invalid repetition config")
    private String repetitionConfig;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @EnumValidator(enumClass = TaskStatus.class, message = "Invalid status")
    private String status;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours (1440 minutes)")
    private Integer duration;

    @EnumValidator(enumClass = TaskPriority.class, message = "Invalid priority")
    private String priority;
}