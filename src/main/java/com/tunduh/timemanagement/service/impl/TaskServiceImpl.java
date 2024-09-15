package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.TaskService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(TaskRequest taskRequest, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        TaskEntity task = TaskEntity.builder()
                .id(UUID.randomUUID().toString())
                .title(taskRequest.getTitle())
                .energy(taskRequest.getEnergy())
                .repetitionConfig(taskRequest.getRepetitionConfig())
                .notes(taskRequest.getNotes())
                .status(taskRequest.getStatus())
                .duration(taskRequest.getDuration())
                .priority(taskRequest.getPriority())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        TaskEntity savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    public CustomPagination<TaskResponse> getAllTasks(String userId, Pageable pageable, String title, String status) {
        log.debug("getAllTasks called with userId: {}, pageable: {}, title: {}, status: {}", userId, pageable, title, status);

        Specification<TaskEntity> spec = TaskSpecification.getSpecification(title, status, userId);

        log.debug("Executing findAll with specification and pageable");
        Page<TaskEntity> taskPage = taskRepository.findAll(spec, pageable);

        log.debug("Mapping TaskEntity to TaskResponse");
        Page<TaskResponse> taskResponsePage = taskPage.map(this::mapToTaskResponse);

        log.debug("Creating CustomPagination object");
        CustomPagination<TaskResponse> result = new CustomPagination<>(taskResponsePage);

        log.debug("Returning result with {} items", result.getContent().size());
        return result;
    }
    @Override
    public TaskResponse getTaskById(String id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapToTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String id, TaskRequest taskRequest, String userId) {
        TaskEntity task = taskRepository.findByIdAndUserId(id, userId);
        if (task == null) {
            throw new ResourceNotFoundException("Task not found for this user");
        }
        task.setTitle(taskRequest.getTitle());
        task.setEnergy(taskRequest.getEnergy());
        task.setRepetitionConfig(taskRequest.getRepetitionConfig());
        task.setNotes(taskRequest.getNotes());
        task.setStatus(taskRequest.getStatus());
        task.setDuration(taskRequest.getDuration());
        task.setPriority(taskRequest.getPriority());
        task.setUpdatedAt(LocalDateTime.now());
        TaskEntity updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(String id, String userId) {
        TaskEntity task = taskRepository.findByIdAndUserId(id, userId);
        if (task == null) {
            throw new ResourceNotFoundException("Task not found for this user");
        }
        taskRepository.delete(task);
    }

    private TaskResponse mapToTaskResponse(TaskEntity task) {
        return TaskResponse.builder()
                .id(UUID.fromString(task.getId()))
                .title(task.getTitle())
                .energy(task.getEnergy())
                .repetitionConfig(task.getRepetitionConfig())
                .notes(task.getNotes())
                .status(task.getStatus())
                .duration(task.getDuration())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}