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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MissionServiceImpl implements MissionService {
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
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
    public CustomPagination<MissionResponse> getAllMissions(String userId, int page, int size, String sort, String name, String progress, String status) {
        Pageable pageable = createPageable(page, size, sort);
        Specification<MissionEntity> spec = MissionSpecification.getSpecification(name, progress, status, userId);

        Page<MissionEntity> missionPage = missionRepository.findAll(spec, pageable);
        return new CustomPagination<>(missionPage.map(this::mapToMissionResponse));
    }

    @Override
    public MissionResponse getMissionById(String id, String userId) {
        MissionEntity mission = missionRepository.findByIdAndUsersId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found for this user"));
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

    @Override
    @Transactional
    public MissionResponse completeMission(String id, String userId) {
        MissionEntity mission = missionRepository.findByIdAndUsersId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found for this user"));

        if ("Completed".equals(mission.getStatus())) {
            throw new IllegalStateException("Mission is already completed");
        }

        mission.setStatus("Completed");
        mission.setProgress("100%");
        mission.setUpdatedAt(LocalDateTime.now());

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setUserPoint(user.getUserPoint() + mission.getPoint());
        userRepository.save(user);

        MissionEntity completedMission = missionRepository.save(mission);
        return mapToMissionResponse(completedMission);
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

    private Pageable createPageable(int page, int size, String sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            for (String param : sortParams) {
                String[] keyDirection = param.split(":");
                String key = keyDirection[0];
                Sort.Direction direction = keyDirection.length > 1 && keyDirection[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, key));
            }
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }
}