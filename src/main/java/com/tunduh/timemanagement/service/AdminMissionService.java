package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.AdminMissionRequest;
import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminMissionService {
    AdminMissionResponse createMission(AdminMissionRequest request);
    AdminMissionResponse updateMission(String id, AdminMissionRequest request);
    void deleteMission(String id);
    AdminMissionResponse getMissionById(String id);
    Page<AdminMissionResponse> getAllMissions(Pageable pageable);
}