package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MissionService {
    MissionResponse createMission(MissionRequest missionRequest, String userId);

    CustomPagination<MissionResponse> getAllMissions(Pageable pageable, String name, String progress, String status);

    MissionResponse getMissionById(String id);

    MissionResponse updateMission(String id, MissionRequest missionRequest, String userId);

    void deleteMission(String id, String userId);
}
