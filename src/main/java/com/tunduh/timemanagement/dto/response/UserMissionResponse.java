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
public class UserMissionResponse {
    private String id;
    private String userId;
    private String missionId;
    private String missionName;
    private Boolean isCompleted;
    private Boolean isRewardClaimed;
    private LocalDateTime completedAt;
    private LocalDateTime rewardClaimedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}