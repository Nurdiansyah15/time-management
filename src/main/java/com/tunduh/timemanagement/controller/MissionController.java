package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.MissionClaimRequest;
import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionProgressResponse;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.MissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Missions", description = "Mission management operations")
public class MissionController {
    private final MissionService missionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new mission")
    public ResponseEntity<?> createMission(@Valid @RequestBody MissionRequest missionRequest) {
        log.info("Received request to create a new mission: {}", missionRequest.getName());
        MissionResponse createdMission = missionService.createMission(missionRequest);
        log.debug("Mission created with ID: {}", createdMission.getId());
        return Response.renderJSON(createdMission, "Mission created successfully!");
    }

    @PutMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update mission photo")
    public ResponseEntity<?> updatePhoto(@RequestPart("image") MultipartFile file, @PathVariable String id) {
        log.info("Received request to update photo for mission with ID: {}", id);
        MissionResponse updatedMission = missionService.updatePhoto(file, id);
        log.debug("Photo updated for mission with ID: {}", id);
        return Response.renderJSON(updatedMission, "Mission photo updated successfully!");
    }

    @GetMapping
    @Operation(summary = "Get all missions")
    public ResponseEntity<?> getAllMissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {
        log.info("Received request to get all missions. Page: {}, Size: {}, Sort: {}, Name: {}, Status: {}", page, size, sort, name, status);
        CustomPagination<MissionResponse> missions = missionService.getAllMissions(page, size, sort, name, status);
        log.debug("Retrieved {} missions", missions.getTotalElements());
        return Response.renderJSON(missions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a mission by ID")
    public ResponseEntity<?> getMissionById(@PathVariable String id) {
        log.info("Received request to get mission with ID: {}", id);
        MissionResponse mission = missionService.getMissionById(id);
        log.debug("Retrieved mission with ID: {}", id);
        return Response.renderJSON(mission);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a mission")
    public ResponseEntity<?> updateMission(@PathVariable String id, @Valid @RequestBody MissionRequest missionRequest) {
        log.info("Received request to update mission with ID: {}", id);
        MissionResponse updatedMission = missionService.updateMission(id, missionRequest);
        log.debug("Updated mission with ID: {}", id);
        return Response.renderJSON(updatedMission, "Mission updated successfully!");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a mission")
    public ResponseEntity<?> deleteMission(@PathVariable String id) {
        log.info("Received request to delete mission with ID: {}", id);
        missionService.deleteMission(id);
        log.debug("Deleted mission with ID: {}", id);
        return Response.renderJSON(null, "Mission deleted successfully!");
    }

    @PostMapping("/{id}/claim")
    @Operation(summary = "Claim a mission")
    public ResponseEntity<?> claimMission(@PathVariable String id, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Received request to claim mission with ID: {} for user: {}", id, user.getId());
        MissionResponse claimedMission = missionService.claimMission(id, user.getId());
        log.debug("Mission with ID: {} claimed by user: {}", id, user.getId());
        return Response.renderJSON(claimedMission, "Mission claimed successfully!");
    }

    @PostMapping("/{id}/claim-reward")
    @Operation(summary = "Claim mission reward")
    public ResponseEntity<?> claimMissionReward(@PathVariable String id, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Received request to claim reward for mission with ID: {} for user: {}", id, user.getId());
        MissionResponse missionReward = missionService.claimMissionReward(id, user.getId());
        log.debug("Reward for mission with ID: {} claimed by user: {}", id, user.getId());
        return Response.renderJSON(missionReward, "Mission reward claimed successfully!");
    }

    @PostMapping("/claim")
    @Operation(summary = "Claim a mission")
    public ResponseEntity<?> claimMission(@RequestBody MissionClaimRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Received request to claim mission with ID: {} for user: {}", request.getMissionId(), user.getId());
        MissionResponse claimedMission = missionService.claimMission(request.getMissionId(), user.getId());
        log.debug("Mission with ID: {} claimed by user: {}", request.getMissionId(), user.getId());
        return Response.renderJSON(claimedMission, "Mission claimed successfully!");
    }

    @PostMapping("/claim-reward")
    @Operation(summary = "Claim mission reward")
    public ResponseEntity<?> claimMissionReward(@RequestBody MissionClaimRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Received request to claim reward for mission with ID: {} for user: {}", request.getMissionId(), user.getId());
        MissionResponse missionReward = missionService.claimMissionReward(request.getMissionId(), user.getId());
        log.debug("Reward for mission with ID: {} claimed by user: {}", request.getMissionId(), user.getId());
        return Response.renderJSON(missionReward, "Mission reward claimed successfully!");
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "Get progress for a specific mission")
    public ResponseEntity<?> getMissionProgress(@PathVariable String id, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Fetching progress for mission: {} and user: {}", id, user.getId());
        MissionProgressResponse progress = missionService.getMissionProgress(id, user.getId());
        return Response.renderJSON(progress, "Mission progress retrieved successfully");
    }

    @GetMapping("/available")
    @Operation(summary = "Get available missions for the current user")
    public ResponseEntity<?> getAvailableMissions(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Fetching available missions for user: {}", user.getId());
        List<MissionResponse> availableMissions = missionService.getAvailableMissionsForUser(user.getId());
        return Response.renderJSON(availableMissions, "Available missions retrieved successfully");
    }
}