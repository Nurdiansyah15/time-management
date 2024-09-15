package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.MissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Tag(name = "Missions", description = "Mission management operations")
public class MissionController {
    private final MissionService missionService;

    @PostMapping
    @Operation(summary = "Create a new mission")
    public ResponseEntity<?> createMission(@Valid @RequestBody MissionRequest missionRequest, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        MissionResponse createdMission = missionService.createMission(missionRequest, userId);
        return Response.renderJSON(createdMission, "Mission created successfully!");
    }

    @GetMapping
    @Operation(summary = "Get all missions with pagination and filtering")
    public ResponseEntity<?> getAllMissions(
            Authentication authentication,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort parameter (e.g., 'name,asc' or 'createdAt,desc')") @RequestParam(required = false) String sort,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by progress") @RequestParam(required = false) String progress,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        CustomPagination<MissionResponse> paginatedMissions = missionService.getAllMissions(userId, page, size, sort, name, progress, status);
        return Response.renderJSON(paginatedMissions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a mission by ID")
    public ResponseEntity<?> getMissionById(@PathVariable String id, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        MissionResponse mission = missionService.getMissionById(id, userId);
        return Response.renderJSON(mission);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a mission")
    public ResponseEntity<?> updateMission(@PathVariable String id, @Valid @RequestBody MissionRequest missionRequest, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        MissionResponse updatedMission = missionService.updateMission(id, missionRequest, userId);
        return Response.renderJSON(updatedMission, "Mission updated successfully!");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mission")
    public ResponseEntity<?> deleteMission(@PathVariable String id, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        missionService.deleteMission(id, userId);
        return Response.renderJSON(null, "Mission deleted successfully!");
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a mission")
    public ResponseEntity<?> completeMission(@PathVariable String id, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();

        MissionResponse completedMission = missionService.completeMission(id, userId);
        return Response.renderJSON(completedMission, "Mission completed successfully!");
    }
}