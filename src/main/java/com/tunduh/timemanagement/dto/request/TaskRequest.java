package com.tunduh.timemanagement.dto.request;

import com.tunduh.timemanagement.entity.TaskEntity.RepetitionType;
import com.tunduh.timemanagement.validation.EnumValidator;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @NotNull(message = "Energy is required")
    @Min(value = 1, message = "Energy must be at least 1")
    @Max(value = 100, message = "Energy cannot exceed 100")
    private Integer energy;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours (1440 minutes)")
    private Integer duration;

    @NotBlank(message = "Priority is required")
    private String priority;

    @EnumValidator(enumClass = RepetitionType.class, message = "Invalid repetition type")
    private RepetitionType repetitionType;

    private Set<@Min(1) @Max(7) Integer> repetitionDays;

    @Future(message = "Repetition end date must be in the future")
    private LocalDateTime repetitionEndDate;
}