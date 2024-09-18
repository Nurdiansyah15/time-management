package com.tunduh.timemanagement.dto.request;

import com.tunduh.timemanagement.entity.TaskEntity.RepetitionType;
import com.tunduh.timemanagement.validation.EnumValidator;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
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
    @Pattern(regexp = "^(PENDING|IN_PROGRESS|COMPLETED)$", message = "Invalid status")
    private String status;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 24 hours (1440 minutes)")
    private Integer duration;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "Invalid priority")
    private String priority;

    @EnumValidator(enumClass = RepetitionType.class, message = "Invalid repetition type")
    private RepetitionType repetitionType;

    @Size(min = 1, max = 7, message = "Repetition days must be between 1 and 7")
    private Set<@Min(1) @Max(7) Integer> repetitionDays;

    @FutureOrPresent(message = "Repetition start date must be in the present or future")
    private LocalDate repetitionStartDate;

    @Future(message = "Repetition end date must be in the future")
    private LocalDate repetitionEndDate;

    @Min(value = 1, message = "Repetition interval must be at least 1")
    private Integer repetitionInterval;
}