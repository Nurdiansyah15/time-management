package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskResponse createTask(TaskRequest taskRequest, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaskEntity task = TaskEntity.builder()
                .id(String.valueOf(UUID.randomUUID()))
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

    public List<TaskResponse> getAllTasks(String userId) {
        List<TaskEntity> tasks = taskRepository.findByUserId(userId);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }


    public TaskResponse getTaskById(String id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return mapToTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(String id, TaskRequest taskRequest, String userId) {
        Optional<TaskEntity> optionalTask = Optional.ofNullable(taskRepository.findByIdAndUserId(id, userId));

        if (optionalTask.isEmpty()) {
            throw new ResourceNotFoundException("Task not found for this user");
        }

        TaskEntity task = optionalTask.get();

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

    @Transactional
    public void deleteTask(String id, String userId) {
        TaskEntity task = taskRepository.findByIdAndUserId(id, userId);
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