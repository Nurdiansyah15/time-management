package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.UserMissionResponse;

import java.util.List;

public interface UserMissionService {
    List<UserMissionResponse> getUserMissions(String userId);
    UserMissionResponse getUserMissionProgress(String userId, String missionId);
    UserMissionResponse claimMission(String userId, String missionId);
    UserMissionResponse completeMission(String userId, String missionId);
    UserMissionResponse claimMissionReward(String userId, String missionId);
}