package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.AdminMissionRequest;
import com.tunduh.timemanagement.dto.response.AdminMissionResponse;
import com.tunduh.timemanagement.entity.MissionEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.MissionRepository;
import com.tunduh.timemanagement.service.AdminMissionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
                .criteriaValue(request.getCriteriaValue())
                .criteriaCompleted(0)
                .type(request.getType())
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
        mission.setCriteriaValue(request.getCriteriaValue());
        mission.setCriteriaCompleted(0);
        mission.setType(request.getType());
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
    public CustomPagination<AdminMissionResponse> getAllMissions(int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);

        Page<MissionEntity> result = missionRepository.findAll(pageable);

        return new CustomPagination<>(result.map(this::mapToAdminMissionResponse));
    }

    private AdminMissionResponse mapToAdminMissionResponse(MissionEntity mission) {
        return AdminMissionResponse.builder()
                .id(mission.getId())
                .name(mission.getName())
                .description(mission.getDescription())
                .pointReward(mission.getPointReward())
                .criteriaValue(mission.getCriteriaValue())
                .criteriaCompleted(mission.getCriteriaCompleted())
                .type(mission.getType())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .status(mission.getStatus())
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