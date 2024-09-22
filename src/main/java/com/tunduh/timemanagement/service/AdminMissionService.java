package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.AdminMissionRequest;
import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

public interface AdminMissionService {
    AdminMissionResponse createMission(AdminMissionRequest request);
    AdminMissionResponse updatePhoto(MultipartFile file, String id);
    AdminMissionResponse updateMission(String id, AdminMissionRequest request);
    void deleteMission(String id);
    AdminMissionResponse getMissionById(String id);
    CustomPagination<AdminMissionResponse> getAllMissions(int page, int size, String sort);
}