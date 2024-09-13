package com.tunduh.timemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MissionRequest {

    @NotBlank(message = "Nama misi harus diisi")
    private String name;

    @NotNull(message = "Poin misi harus diisi")
    private Integer point;
}