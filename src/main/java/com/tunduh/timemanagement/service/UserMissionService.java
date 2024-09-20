package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import com.tunduh.timemanagement.dto.response.UserMissionResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;

public interface UserMissionService {
    UserMissionResponse getUserMissionProgress(String userId, String missionId);
    UserMissionResponse claimMission(String userId, String missionId);
    UserMissionResponse completeMission(String userId, String missionId);
    UserMissionResponse claimMissionReward(String userId, String missionId);
    CustomPagination<UserMissionResponse> getClaimedMissions(String userId, int page, int size);
    CustomPagination<AdminMissionResponse> getAvailableMissions(String userId, int page, int size);
}