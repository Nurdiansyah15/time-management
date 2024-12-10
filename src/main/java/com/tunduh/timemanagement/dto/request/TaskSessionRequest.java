package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskSessionRequest {
    @NotNull(message = "Task ID is required")
    private String taskId;

    private String notes;
}