package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MissionRequest {

   @NotBlank(message = "Mission name is required")
    private String name;

    @NotNull(message = "Mission points must be filled")
    private Integer point;
}