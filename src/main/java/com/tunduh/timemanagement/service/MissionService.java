package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionProgressResponse;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MissionService {
    MissionResponse createMission(MissionRequest missionRequest);
    MissionResponse updatePhoto(MultipartFile file, String id);
    CustomPagination<MissionResponse> getAllMissions(int page, int size, String sort, String name, String status);
    MissionResponse getMissionById(String id);
    MissionResponse updateMission(String id, MissionRequest missionRequest);
    void deleteMission(String id);
    MissionResponse claimMission(String id, String userId);
    MissionResponse claimMissionReward(String id, String userId);
    void checkAndUpdateMissions();
    List<MissionResponse> getAvailableMissionsForUser(String userId);
    MissionProgressResponse getMissionProgress(String missionId, String userId);
}