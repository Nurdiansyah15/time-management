package com.tunduh.timemanagement.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Energy is required")
    private Integer energy;

    private String repetitionConfig;

    private String notes;

    private String status;

    private Integer duration;

    private String priority;
}