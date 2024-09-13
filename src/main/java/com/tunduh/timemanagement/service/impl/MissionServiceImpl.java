package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.MissionRequest;
import com.tunduh.timemanagement.dto.response.MissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.MissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.specification.MissionSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    @Override
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

    @Override
    public CustomPagination<MissionResponse> getAllMissions(Pageable pageable, String name, String progress, String status) {
        Specification<MissionEntity> specification = MissionSpecification.getSpecification(name, progress, status);
        Page<MissionEntity> missionPage = missionRepository.findAll(specification, pageable);

        List<MissionResponse> missionResponses = missionPage.getContent().stream()
                .map(this::mapToMissionResponse)
                .collect(Collectors.toList());

        Page<MissionResponse> responsePage = new PageImpl<>(
                missionResponses,
                pageable,
                missionPage.getTotalElements()
        );

        return new CustomPagination<>(responsePage);
    }


//    @Overide
//    public List<MissionResponse> getAllMissions(String userId) {
//        List<MissionEntity> missions = missionRepository.findByUsersId(userId);
//        return missions.stream()
//                .map(this::mapToMissionResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public MissionResponse getMissionById(String id) {
        MissionEntity mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found"));
        return mapToMissionResponse(mission);
    }

    @Override
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

    @Override
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
