package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.AdminMissionRequest;
import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.service.AdminMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMissionServiceImpl implements AdminMissionService {

    private final MissionRepository missionRepository;

    @Override
    @Transactional
    public AdminMissionResponse createMission(AdminMissionRequest request) {
        MissionEntity mission = MissionEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .pointReward(request.getPointReward())
                .requiredTaskCount(request.getRequiredTaskCount())
                .requiredDuration(request.getRequiredDuration())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(MissionEntity.MissionStatus.ACTIVE)
                .build();

        MissionEntity savedMission = missionRepository.save(mission);
        return mapToAdminMissionResponse(savedMission);
    }

    @Override
    @Transactional
    public AdminMissionResponse updateMission(String id, AdminMissionRequest request) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));

        mission.setName(request.getName());
        mission.setDescription(request.getDescription());
        mission.setPointReward(request.getPointReward());
        mission.setRequiredTaskCount(request.getRequiredTaskCount());
        mission.setRequiredDuration(request.getRequiredDuration());
        mission.setStartDate(request.getStartDate());
        mission.setEndDate(request.getEndDate());

        MissionEntity updatedMission = missionRepository.save(mission);
        return mapToAdminMissionResponse(updatedMission);
    }

    @Override
    @Transactional
    public void deleteMission(String id) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        missionRepository.delete(mission);
    }

    @Override
    public AdminMissionResponse getMissionById(String id) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        return mapToAdminMissionResponse(mission);
    }

    @Override
    public Page<AdminMissionResponse> getAllMissions(Pageable pageable) {
        return missionRepository.findAll(pageable)
                .map(this::mapToAdminMissionResponse);
    }

    private AdminMissionResponse mapToAdminMissionResponse(MissionEntity mission) {
        return AdminMissionResponse.builder()
                .id(mission.getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .pointReward(mission.getPointReward())
                .requiredTaskCount(mission.getRequiredTaskCount())
                .requiredDuration(mission.getRequiredDuration())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .status(mission.getStatus().name())
                .createdAt(mission.getCreatedAt())
                .updatedAt(mission.getUpdatedAt())
                .build();
    }
}