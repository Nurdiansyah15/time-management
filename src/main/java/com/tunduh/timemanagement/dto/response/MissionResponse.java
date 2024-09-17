package com.tunduh.timemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MissionResponse {

    private String id;
    private String name;
    private String progress;
    private String status;
    private Integer point;
    private String missionPicture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}