package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.AdminMissionRequest;
import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import com.tunduh.timemanagement.service.AdminMissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/missions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Missions", description = "Admin mission management operations")
public class AdminMissionController {

    private final AdminMissionService adminMissionService;

    @PostMapping
    @Operation(summary = "Create a new mission")
    public ResponseEntity<?> createMission(@Valid @RequestBody AdminMissionRequest request) {
        AdminMissionResponse createdMission = adminMissionService.createMission(request);
        return Response.renderJSON(createdMission, "Mission created successfully");
    }

    @PutMapping("/{id}/photos")
    @Operation(summary = "Update shop item photo")
    public ResponseEntity<?> updatePhoto(@RequestPart("image") MultipartFile file, @PathVariable String id) {
        return Response.renderJSON(adminMissionService.updatePhoto(file, id), "Photo updated successfully");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing mission")
    public ResponseEntity<?> updateMission(@PathVariable String id, @Valid @RequestBody AdminMissionRequest request) {
        AdminMissionResponse updatedMission = adminMissionService.updateMission(id, request);
        return Response.renderJSON(updatedMission, "Mission updated successfully");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mission")
    public ResponseEntity<?> deleteMission(@PathVariable String id) {
        adminMissionService.deleteMission(id);
        return Response.renderJSON(null, "Mission deleted successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a mission by ID")
    public ResponseEntity<?> getMissionById(@PathVariable String id) {
        AdminMissionResponse mission = adminMissionService.getMissionById(id);
        return Response.renderJSON(mission);
    }

    @GetMapping
    @Operation(summary = "Get all missions")
    public ResponseEntity<?> getAllMissions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                                             @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
                                             @Parameter(description = "Sort parameter (e.g., 'title,asc' or 'createdAt,desc')") @RequestParam(required = false) String sort
    ) {
        CustomPagination<AdminMissionResponse> missions = adminMissionService.getAllMissions(page,size,sort);

        return Response.renderJSON(missions);
    }
}