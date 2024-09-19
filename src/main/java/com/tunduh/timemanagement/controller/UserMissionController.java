package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.response.UserMissionResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.UserMissionService;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-missions")
@RequiredArgsConstructor
@Tag(name = "User Missions", description = "User mission management operations")
public class UserMissionController {

    private final UserMissionService userMissionService;

    @GetMapping
    @Operation(summary = "Get all missions for the current user")
    public ResponseEntity<?> getUserMissions(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        List<UserMissionResponse> userMissions = userMissionService.getUserMissions(user.getId());
        return Response.renderJSON(userMissions, "User missions retrieved successfully");
    }

    @GetMapping("/{missionId}")
    @Operation(summary = "Get specific mission progress for the current user")
    public ResponseEntity<?> getUserMissionProgress(@PathVariable String missionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserMissionResponse userMission = userMissionService.getUserMissionProgress(user.getId(), missionId);
        return Response.renderJSON(userMission, "User mission progress retrieved successfully");
    }

    @PostMapping("/{missionId}/claim")
    @Operation(summary = "Claim a mission for the current user")
    public ResponseEntity<?> claimMission(@PathVariable String missionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserMissionResponse claimedMission = userMissionService.claimMission(user.getId(), missionId);
        return Response.renderJSON(claimedMission, "Mission claimed successfully");
    }

    @PostMapping("/{missionId}/complete")
    @Operation(summary = "Mark a mission as completed for the current user")
    public ResponseEntity<?> completeMission(@PathVariable String missionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserMissionResponse completedMission = userMissionService.completeMission(user.getId(), missionId);
        return Response.renderJSON(completedMission, "Mission completed successfully");
    }

    @PostMapping("/{missionId}/claim-reward")
    @Operation(summary = "Claim reward for a completed mission")
    public ResponseEntity<?> claimMissionReward(@PathVariable String missionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserMissionResponse rewardClaimedMission = userMissionService.claimMissionReward(user.getId(), missionId);
        return Response.renderJSON(rewardClaimedMission, "Mission reward claimed successfully");
    }
}