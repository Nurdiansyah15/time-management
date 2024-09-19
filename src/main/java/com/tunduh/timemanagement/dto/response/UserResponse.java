package com.tunduh.timemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private String id;
    private String username;
    private String email;
    private String profilePicture;
    private Integer userPoint;
    private LocalTime resetTime;
    private long claimedMissions;
    private long completedMissions;
    private long unclaimedRewards;
}