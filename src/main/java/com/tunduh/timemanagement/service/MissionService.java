package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;

import java.util.List;

public interface MissionService {
    MissionResponse createMission(MissionRequest missionRequest, String userId);

    List<MissionResponse> getAllMissions(String userId);

    MissionResponse getMissionById(String id);

    MissionResponse updateMission(String id, MissionRequest missionRequest, String userId);

    void deleteMission(String id, String userId);
}
