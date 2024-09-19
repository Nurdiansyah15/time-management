package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    private Integer point;

    private Integer criteriaCompleted;

    @NotBlank(message = "Type is required")
    private String type;
}
