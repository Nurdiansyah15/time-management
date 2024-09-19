package com.tunduh.timemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MissionResponse implements Serializable {
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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isClaimed;
    private Boolean isRewardClaimed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}