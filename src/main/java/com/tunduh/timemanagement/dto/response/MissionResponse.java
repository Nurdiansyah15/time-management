package com.tunduh.timemanagement.dto.response;

import jakarta.persistence.Column;
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
    private String description;
    private String status;
    private Integer pointReward;
    private String missionPicture;
    private Integer requiredTaskCount;
    private Integer requiredDuration;
    private Boolean isDurationOnly;
    private Boolean isTaskOnly;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}