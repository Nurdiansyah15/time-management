package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.AdminMissionRequest;
import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;

public interface AdminMissionService {
    AdminMissionResponse createMission(AdminMissionRequest request);
    AdminMissionResponse updateMission(String id, AdminMissionRequest request);
    void deleteMission(String id);
    AdminMissionResponse getMissionById(String id);
    CustomPagination<AdminMissionResponse> getAllMissions(int page, int size, String sort);
}