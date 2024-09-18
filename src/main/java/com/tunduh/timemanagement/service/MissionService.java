package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

public interface MissionService {
    MissionResponse createMission(MissionRequest missionRequest, String userId);
    MissionResponse updatePhoto(MultipartFile file, String id);
    CustomPagination<MissionResponse> getAllMissions(String userId, int page, int size, String sort, String name, String status);
    MissionResponse getMissionById(String id, String userId);
    MissionResponse updateMission(String id, MissionRequest missionRequest, String userId);
    void deleteMission(String id, String userId);
    MissionResponse completeMission(String id, String userId);
    MissionResponse addSubMission(String parentId, MissionRequest subMissionRequest, String userId);
    void removeSubMission(String parentId, String subMissionId, String userId);
}