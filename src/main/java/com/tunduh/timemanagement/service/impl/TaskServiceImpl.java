package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.entity.ShopItemEntity;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.TaskService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.specification.TaskSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;


    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TaskEntity task = TaskEntity.builder()
                .id(UUID.randomUUID().toString())
                .title(taskRequest.getTitle())
                .energy(taskRequest.getEnergy())
                .notes(taskRequest.getNotes())
                .status(taskRequest.getStatus())
                .duration(taskRequest.getDuration())
                .priority(taskRequest.getPriority())
                .repetitionType(TaskEntity.RepetitionType.valueOf(taskRequest.getRepetitionType()))
                .repetitionDays(taskRequest.getRepetitionDays())
                .repetitionEndDate(taskRequest.getRepetitionEndDate())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskEntity savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updatePhoto(MultipartFile file, String id) {
        TaskEntity taskItem = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task item with id " + id + " not found"));
        String url = cloudinaryService.uploadFile(file, "task");
        taskItem.setTaskPicture(url);
        TaskEntity savedTaskItem = taskRepository.save(taskItem);
        return mapToTaskResponse(savedTaskItem);
    }

    @Override
    public CustomPagination<TaskResponse> getAllTasks(String userId, int page, int size, String sort, String title, String status) {
        Pageable pageable = createPageable(page, size, sort);
        Specification<TaskEntity> spec = TaskSpecification.getSpecification(title, status, userId);

        Page<TaskEntity> taskPage = taskRepository.findAll(spec, pageable);
        List<TaskResponse> taskResponses = taskPage.getContent().stream()
                .map(this::mapToTaskResponse)
                .toList();

        return new CustomPagination<>(taskPage.map(this::mapToTaskResponse));
    }

    @Override
    public TaskResponse getTaskById(String id, String userId) {
        TaskEntity task = taskRepository.findByIdAndUserId(id, userId);
        if (task == null) {
            throw new ResourceNotFoundException("Task not found for this user");
        }
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
        task.setNotes(taskRequest.getNotes());
        task.setStatus(taskRequest.getStatus());
        task.setDuration(taskRequest.getDuration());
        task.setPriority(taskRequest.getPriority());
        task.setRepetitionType(TaskEntity.RepetitionType.valueOf(taskRequest.getRepetitionType()));
        task.setRepetitionDays(taskRequest.getRepetitionDays());
        task.setRepetitionEndDate(taskRequest.getRepetitionEndDate());
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
                .notes(task.getNotes())
                .status(task.getStatus())
                .duration(task.getDuration())
                .priority(task.getPriority())
                .repetitionType(task.getRepetitionType())
                .repetitionDays(task.getRepetitionDays())
                .repetitionEndDate(task.getRepetitionEndDate())
                .taskPicture(task.getTaskPicture())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
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