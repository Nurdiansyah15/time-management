package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Point is required")
    @Min(value = 0, message = "Point must be non-negative")
    private Integer point;

    @NotNull(message = "Criteria completed is required")
    @Min(value = 0, message = "Criteria completed must be non-negative")
    private Integer criteriaCompleted;

    @NotBlank(message = "Type is required")
    private String type;
}