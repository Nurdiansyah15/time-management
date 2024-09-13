package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    public MissionResponse createMission(MissionRequest missionRequest, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MissionEntity mission = MissionEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(missionRequest.getName())
                .progress("0%")
                .status("In Progress")
                .point(missionRequest.getPoint())
                .users(Set.of(user))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MissionEntity savedMission = missionRepository.save(mission);
        return mapToMissionResponse(savedMission);
    }

    public List<MissionResponse> getAllMissions(String userId) {
        List<MissionEntity> missions = missionRepository.findByUsersId(userId);
        return missions.stream()
                .map(this::mapToMissionResponse)
                .collect(Collectors.toList());
    }

    public MissionResponse getMissionById(String id) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        return mapToMissionResponse(mission);
    }

    @Transactional
    public MissionResponse updateMission(String id, MissionRequest missionRequest, String userId) {
        MissionEntity mission = missionRepository.findByIdAndUsersId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found for this user"));

        mission.setName(missionRequest.getName());
        mission.setPoint(missionRequest.getPoint());
        mission.setUpdatedAt(LocalDateTime.now());

        MissionEntity updatedMission = missionRepository.save(mission);
        return mapToMissionResponse(updatedMission);
    }

    @Transactional
    public void deleteMission(String id, String userId) {
        MissionEntity mission = missionRepository.findByIdAndUsersId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found for this user"));
        missionRepository.delete(mission);
    }

    private MissionResponse mapToMissionResponse(MissionEntity mission) {
        return MissionResponse.builder()
                .id(mission.getId())
                .name(mission.getName())
                .progress(mission.getProgress())
                .status(mission.getStatus())
                .point(mission.getPoint())
                .createdAt(mission.getCreatedAt())
                .updatedAt(mission.getUpdatedAt())
                .build();
    }
}