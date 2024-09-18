package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.MissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Tag(name = "Missions", description = "Mission management operations")
public class MissionController {
    private final MissionService missionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new mission")
    public ResponseEntity<?> createMission(@Valid @RequestBody MissionRequest missionRequest) {
        MissionResponse createdMission = missionService.createMission(missionRequest);
        return Response.renderJSON(createdMission, "Mission created successfully!");
    }

    @PutMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update mission photo")
    public ResponseEntity<?> updatePhoto(@RequestPart("image") MultipartFile file, @PathVariable String id) {
        MissionResponse updatedMission = missionService.updatePhoto(file, id);
        return Response.renderJSON(updatedMission, "Mission photo updated successfully!");
    }

    // TODO:
}