package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.service.MissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping
    public ResponseEntity<?> createMission(@Valid @RequestBody MissionRequest missionRequest, Authentication authentication) {
        String userId = authentication.getName();
        MissionResponse createdMission = missionService.createMission(missionRequest, userId);
        return Response.renderJSON(createdMission, "Mission created successfully!");
    }

//    @GetMapping
//    public ResponseEntity<?> getAllMissions(Authentication authentication) {
//        String userId = authentication.getName();
//        List<MissionResponse> missions = missionService.getAllMissions(userId);
//        return Response.renderJSON(missions);
//    }

    @GetMapping
    public ResponseEntity<CustomPagination<MissionResponse>> getAllMissions(
            Authentication authentication,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String progress,
            @RequestParam(required = false) String status) {

        String userId = authentication.getName();
        CustomPagination<MissionResponse> paginatedMissions = missionService.getAllMissions(pageable, name, progress, status);

        return ResponseEntity.ok(paginatedMissions);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getMissionById(@PathVariable String id) {
        MissionResponse mission = missionService.getMissionById(id);
        return Response.renderJSON(mission);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMission(@PathVariable String id, @Valid @RequestBody MissionRequest missionRequest, Authentication authentication) {
        String userId = authentication.getName();
        MissionResponse updatedMission = missionService.updateMission(id, missionRequest, userId);
        return Response.renderJSON(updatedMission, "Mission updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMission(@PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        missionService.deleteMission(id, userId);
        return Response.renderJSON(null, "Mission deleted successfully!");
    }
}