package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.request.TaskSessionRequest;
import com.tunduh.timemanagement.dto.request.TaskSessionSyncRequest;
import com.tunduh.timemanagement.dto.request.TaskSyncRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.dto.response.TaskSessionResponse;
import com.tunduh.timemanagement.dto.response.TaskSyncResponse;
import com.tunduh.timemanagement.entity.TaskEntity;
import com.tunduh.timemanagement.entity.TaskSessionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.TaskRepository;
import com.tunduh.timemanagement.repository.TaskSessionRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.EnergyManagementService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskSessionRepository taskSessionRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final EnergyManagementService energyManagementService;

    @Transactional
    @Override
    @Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    public void generateRecurringTasks() {
        LocalDate today = LocalDate.now();
        List<TaskEntity> recurringTasks = taskRepository.findAllActiveRecurringTasks(today.atStartOfDay());

        for (TaskEntity task : recurringTasks) {
            if (shouldGenerateTaskForToday(task, today)) {
                createNewTaskInstance(task, today);
            }
        }
    }

    private boolean shouldGenerateTaskForToday(TaskEntity task, LocalDate today) {
        if (today.isBefore(task.getRepetitionStartDate().toLocalDate()) ||
                (task.getRepetitionEndDate() != null && today.isAfter(task.getRepetitionEndDate().toLocalDate()))) {
            return false;
        }

        switch (task.getRepetitionType()) {
            case DAILY:
                return true;
            case WEEKLY:
                return task.getRepetitionDates().contains(today.getDayOfWeek().getValue());
            case MONTHLY:
                return task.getRepetitionDates().contains(today.getDayOfMonth());
            case YEARLY:
                return task.getRepetitionDates().contains(today.getDayOfYear());
            case CUSTOM:
                long daysBetween = ChronoUnit.DAYS.between(task.getRepetitionStartDate().toLocalDate(), today);
                return daysBetween % task.getRepetitionInterval() == 0;
            default:
                return false;
        }
    }

    private void createNewTaskInstance(TaskEntity originalTask, LocalDate today) {
        TaskEntity newTask = TaskEntity.builder()
                .id(UUID.randomUUID().toString())
                .title(originalTask.getTitle())
                .energy(originalTask.getEnergy())
                .notes(originalTask.getNotes())
                .status("PENDING")
                .duration(originalTask.getDuration())
                .priority(originalTask.getPriority())
                .user(originalTask.getUser())
                .repetitionType(originalTask.getRepetitionType())
                .repetitionDates(originalTask.getRepetitionDates())
                .repetitionStartDate(originalTask.getRepetitionStartDate())
                .repetitionEndDate(originalTask.getRepetitionEndDate())
                .repetitionInterval(originalTask.getRepetitionInterval())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        taskRepository.save(newTask);
    }

    @Override
    @Transactional
    public TaskSessionResponse startTask(TaskSessionRequest request, String userId) {
        TaskEntity task = getTaskForUser(request.getTaskId(), userId);
        TaskSessionEntity session = TaskSessionEntity.builder()
                .task(task)
                .startTime(LocalDateTime.now())
                .durationInSeconds(0L)
                .status(TaskSessionEntity.SessionStatus.IN_PROGRESS)
                .notes(request.getNotes())
                .build();
        return mapToTaskSessionResponse(taskSessionRepository.save(session));
    }

    @Override
    @Transactional
    public TaskSessionResponse pauseTask(String sessionId, String userId) {
        TaskSessionEntity session = getTaskSessionForUser(sessionId, userId);
        if (session.getStatus() != TaskSessionEntity.SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task is not in progress");
        }
        session.setStatus(TaskSessionEntity.SessionStatus.PAUSED);
        session.setDurationInSeconds(session.getDurationInSeconds() +
                Duration.between(session.getStartTime(), LocalDateTime.now()).getSeconds());
        return mapToTaskSessionResponse(taskSessionRepository.save(session));
    }

    @Override
    @Transactional
    public TaskSessionResponse resumeTask(String sessionId, String userId) {
        TaskSessionEntity session = getTaskSessionForUser(sessionId, userId);
        if (session.getStatus() != TaskSessionEntity.SessionStatus.PAUSED) {
            throw new IllegalStateException("Task is not paused");
        }
        session.setStatus(TaskSessionEntity.SessionStatus.IN_PROGRESS);
        session.setStartTime(LocalDateTime.now());
        return mapToTaskSessionResponse(taskSessionRepository.save(session));
    }

    @Override
    @Transactional
    public TaskSessionResponse stopTask(String sessionId, String userId) {
        TaskSessionEntity session = getTaskSessionForUser(sessionId, userId);
        if (session.getStatus() == TaskSessionEntity.SessionStatus.COMPLETED) {
            throw new IllegalStateException("Task is already completed");
        }
        session.setStatus(TaskSessionEntity.SessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());
        if (session.getStatus() == TaskSessionEntity.SessionStatus.IN_PROGRESS) {
            session.setDurationInSeconds(session.getDurationInSeconds() +
                    Duration.between(session.getStartTime(), session.getEndTime()).getSeconds());
        }

        TaskSessionEntity savedSession = taskSessionRepository.save(session);

        TaskEntity task = session.getTask();
        energyManagementService.decreaseEnergy(userId, task.getEnergy());

        checkAndUpdateTaskCompletion(session.getTask());

        return mapToTaskSessionResponse(savedSession);
    }

    private void checkAndUpdateTaskCompletion(TaskEntity task) {
        long totalSessionDuration = taskSessionRepository.findByTaskId(task.getId()).stream()
                .filter(session -> session.getStatus() == TaskSessionEntity.SessionStatus.COMPLETED)
                .mapToLong(TaskSessionEntity::getDurationInSeconds)
                .sum();

        long taskDurationInSeconds = task.getDuration() * 60L; // Convert minutes to seconds

        if (totalSessionDuration >= taskDurationInSeconds && !task.getStatus().equals("COMPLETED")) {
            task.setStatus("COMPLETED");
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
            log.info("Task {} marked as completed. Total session duration: {} seconds, Required duration: {} seconds",
                    task.getId(), totalSessionDuration, taskDurationInSeconds);
        }
    }

    @Override
    @Transactional
    public TaskSessionResponse updateTaskSessionNotes(String sessionId, String notes, String userId) {
        TaskSessionEntity session = getTaskSessionForUser(sessionId, userId);
        session.setNotes(notes);
        return mapToTaskSessionResponse(taskSessionRepository.save(session));
    }

    @Override
    public CustomPagination<TaskSessionResponse> getTaskSessions(String taskId, String userId, int page, int size) {
        TaskEntity task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or you don't have access to it"));

        Page<TaskSessionEntity> sessionPage = taskSessionRepository.findByTaskId(taskId, PageRequest.of(page, size));

        return new CustomPagination<>(sessionPage.map(this::mapToTaskSessionResponse));
    }

    private TaskEntity getTaskForUser(String taskId, String userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found for this user"));
    }

    private TaskSessionEntity getTaskSessionForUser(String sessionId, String userId) {
        return taskSessionRepository.findByIdAndTaskUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task session not found for this user"));
    }

    private TaskSessionResponse mapToTaskSessionResponse(TaskSessionEntity session) {
        return TaskSessionResponse.builder()
                .id(session.getId())
                .taskId(session.getTask().getId())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .durationInSeconds(session.getDurationInSeconds())
                .status(session.getStatus())
                .notes(session.getNotes())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public List<TaskSyncResponse> synchronizeTasks(String userId, List<TaskSyncRequest> syncRequests) {
        List<TaskSyncResponse> responses = new ArrayList<>();
        List<String> taskIds = syncRequests.stream()
                .map(TaskSyncRequest::getTaskId)
                .collect(Collectors.toList());

        List<TaskEntity> tasks = taskRepository.findByIdIn(taskIds);

        Map<String, TaskEntity> existingTasks = tasks.stream()
                .collect(Collectors.toMap(TaskEntity::getId, task -> task));

        for (TaskSyncRequest syncRequest : syncRequests) {
            TaskEntity task = existingTasks.get(syncRequest.getTaskId());
            if (task == null) {
                // Create new task
                task = createNewTask(syncRequest, userId);
            } else if (syncRequest.getVersion() > task.getVersion()) {
                // Update existing task
                updateExistingTask(task, syncRequest);
            }

            // Sync task sessions
            List<TaskSessionEntity> updatedSessions = syncTaskSessions(task, syncRequest.getSessions());

            // Prepare response
            responses.add(createTaskSyncResponse(task, updatedSessions));
        }

        return responses;
    }

    private TaskEntity createNewTask(TaskSyncRequest syncRequest, String userId) {
        TaskEntity newTask = new TaskEntity();
        updateTaskFromSyncRequest(newTask, syncRequest);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        newTask.setUser(user);

        return taskRepository.save(newTask);
    }

    private void updateExistingTask(TaskEntity task, TaskSyncRequest syncRequest) {
        updateTaskFromSyncRequest(task, syncRequest);
        taskRepository.save(task);
    }

    private void updateTaskFromSyncRequest(TaskEntity task, TaskSyncRequest syncRequest) {
        task.setTitle(syncRequest.getTitle());
        task.setStatus(syncRequest.getStatus());
        task.setEnergy(syncRequest.getEnergy());
        task.setNotes(syncRequest.getNotes());
        task.setDuration(syncRequest.getDuration());
        task.setPriority(syncRequest.getPriority());
        task.setVersion(syncRequest.getVersion());
        task.setUpdatedAt(LocalDateTime.now());
    }

    private List<TaskSessionEntity> syncTaskSessions(TaskEntity task, List<TaskSessionSyncRequest> sessionRequests) {
        List<TaskSessionEntity> updatedSessions = new ArrayList<>();
        Map<String, TaskSessionEntity> existingSessions = task.getSessions().stream()
                .collect(Collectors.toMap(TaskSessionEntity::getId, session -> session));

        for (TaskSessionSyncRequest sessionRequest : sessionRequests) {
            TaskSessionEntity session = existingSessions.get(sessionRequest.getSessionId());
            if (session == null) {
                session = createNewTaskSession(task, sessionRequest);
            } else if (sessionRequest.getVersion() > session.getVersion()) {
                updateExistingTaskSession(session, sessionRequest);
            }
            updatedSessions.add(session);
        }

        return updatedSessions;
    }

    private TaskSessionEntity createNewTaskSession(TaskEntity task, TaskSessionSyncRequest sessionRequest) {
        TaskSessionEntity newSession = new TaskSessionEntity();
        updateTaskSessionFromSyncRequest(newSession, sessionRequest);
        newSession.setTask(task);
        return taskSessionRepository.save(newSession);
    }

    private void updateExistingTaskSession(TaskSessionEntity session, TaskSessionSyncRequest sessionRequest) {
        updateTaskSessionFromSyncRequest(session, sessionRequest);
        taskSessionRepository.save(session);
    }

    private void updateTaskSessionFromSyncRequest(TaskSessionEntity session, TaskSessionSyncRequest sessionRequest) {
        session.setStartTime(sessionRequest.getStartTime());
        session.setEndTime(sessionRequest.getEndTime());
        session.setDurationInSeconds(sessionRequest.getDurationInSeconds());
        session.setStatus(TaskSessionEntity.SessionStatus.valueOf(sessionRequest.getStatus()));
        session.setNotes(sessionRequest.getNotes());
        session.setVersion(sessionRequest.getVersion());
        session.setUpdatedAt(LocalDateTime.now());
    }

    private TaskSyncResponse createTaskSyncResponse(TaskEntity task, List<TaskSessionEntity> sessions) {
        return TaskSyncResponse.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .energy(task.getEnergy())
                .notes(task.getNotes())
                .status(task.getStatus())
                .duration(task.getDuration())
                .priority(task.getPriority())
                .lastSyncedAt(LocalDateTime.now())
                .version(task.getVersion())
                .conflicted(false)
                .build();
    }

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate now = LocalDate.now();

        if (taskRequest.getRepetitionStartDate() != null && taskRequest.getRepetitionStartDate().isBefore(now.atStartOfDay())) {
            throw new IllegalArgumentException("Repetition start date must be today or in the future.");
        }

        if (taskRequest.getRepetitionEndDate() != null && taskRequest.getRepetitionEndDate().isBefore(now.atStartOfDay())) {
            throw new IllegalArgumentException("Repetition end date must be today or in the future.");
        }

        TaskEntity task = TaskEntity.builder()
                .id(UUID.randomUUID().toString())
                .title(taskRequest.getTitle())
                .energy(taskRequest.getEnergy())
                .notes(taskRequest.getNotes())
                .status(taskRequest.getStatus())
                .duration(taskRequest.getDuration())
                .priority(taskRequest.getPriority())
                .repetitionType(taskRequest.getRepetitionType())
                .repetitionDates(taskRequest.getRepetitionDates())
                .repetitionStartDate(taskRequest.getRepetitionStartDate())
                .repetitionEndDate(taskRequest.getRepetitionEndDate())
                .repetitionInterval(taskRequest.getRepetitionInterval())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        validateAndSetRepetitionDates(task, taskRequest.getRepetitionDates());

        TaskEntity savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    private void validateAndSetRepetitionDates(TaskEntity task, Set<Integer> dates) {
        switch (task.getRepetitionType()) {
            case DAILY:
                task.setRepetitionDates(Collections.emptySet());
                break;
            case WEEKLY:
                validateWeeklyDates(dates);
                task.setRepetitionDates(dates);
                break;
            case MONTHLY:
                validateMonthlyDates(dates);
                task.setRepetitionDates(dates);
                break;
            case YEARLY:
                validateYearlyDates(dates);
                task.setRepetitionDates(dates);
                break;
            case CUSTOM:
                if (task.getRepetitionInterval() == null) {
                    throw new IllegalArgumentException("Repetition interval is required for CUSTOM type");
                }
                task.setRepetitionDates(Collections.emptySet());
                break;
            case NONE:
                task.setRepetitionDates(Collections.emptySet());
                task.setRepetitionStartDate(null);
                task.setRepetitionEndDate(null);
                break;
        }
    }

    private void validateWeeklyDates(Set<Integer> dates) {
        if (dates.stream().anyMatch(d -> d < 1 || d > 7)) {
            throw new IllegalArgumentException("Weekly dates must be between 1 and 7");
        }
    }

    private void validateMonthlyDates(Set<Integer> dates) {
        if (dates.stream().anyMatch(d -> d < 1 || d > 31)) {
            throw new IllegalArgumentException("Monthly dates must be between 1 and 31");
        }
    }

    private void validateYearlyDates(Set<Integer> dates) {
        if (dates.stream().anyMatch(d -> d < 1 || d > 366)) {
            throw new IllegalArgumentException("Yearly dates must be between 1 and 366");
        }
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

        return new CustomPagination<>(taskPage.map(this::mapToTaskResponse));
    }

    @Override
    public TaskResponse getTaskById(String id, String userId) {
        TaskEntity task = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found for this user"));
        return mapToTaskResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String id, TaskRequest taskRequest, String userId) {
        TaskEntity task = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found for this user"));

        LocalDate now = LocalDate.now();

        if (taskRequest.getRepetitionStartDate() != null && taskRequest.getRepetitionStartDate().isBefore(now.atStartOfDay())) {
            throw new IllegalArgumentException("Repetition start date must be today or in the future.");
        }

        if (taskRequest.getRepetitionEndDate() != null && taskRequest.getRepetitionEndDate().isBefore(now.atStartOfDay())) {
            throw new IllegalArgumentException("Repetition end date must be today or in the future.");
        }

        task.setTitle(taskRequest.getTitle());
        task.setEnergy(taskRequest.getEnergy());
        task.setNotes(taskRequest.getNotes());
        task.setStatus(taskRequest.getStatus());
        task.setDuration(taskRequest.getDuration());
        task.setPriority(taskRequest.getPriority());
        task.setRepetitionType(taskRequest.getRepetitionType());
        task.setRepetitionDates(taskRequest.getRepetitionDates());
        task.setRepetitionStartDate(taskRequest.getRepetitionStartDate());
        task.setRepetitionEndDate(taskRequest.getRepetitionEndDate());
        task.setRepetitionInterval(taskRequest.getRepetitionInterval());
        task.setUpdatedAt(LocalDateTime.now());

        validateAndSetRepetitionDates(task, taskRequest.getRepetitionDates());

        TaskEntity updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(String id, String userId) {
        TaskEntity task = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found for this user"));
        taskSessionRepository.deleteByTaskId(task.getId());
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
                .repetitionDates(task.getRepetitionDates())
                .repetitionStartDate(task.getRepetitionStartDate())
                .repetitionEndDate(task.getRepetitionEndDate())
                .repetitionInterval(task.getRepetitionInterval())
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