package com.tunduh.timemanagement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.request.TaskSessionRequest;
import com.tunduh.timemanagement.dto.request.TaskSyncRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.dto.response.TaskSessionResponse;
import com.tunduh.timemanagement.dto.response.TaskSyncResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.TaskService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Creating new task for user: {}", user.getId());
        TaskResponse createdTask = taskService.createTask(taskRequest, user.getId());
        return Response.renderJSON(createdTask, "Task created successfully!");
    }



    @PutMapping("/{id}/photos")
    @Operation(summary = "Update photo")
    public ResponseEntity<?> updatePhoto(
            @RequestPart("images") MultipartFile file,
            @PathVariable String id) throws JsonProcessingException {
        return Response.renderJSON(taskService.updatePhoto(file, id), "PHOTOS UPLOADED");
    }

    @GetMapping
    @Operation(summary = "Get all tasks with pagination and filtering")
    public ResponseEntity<?> getAllTasks(
            Authentication authentication,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort parameter (e.g., 'title,asc' or 'createdAt,desc')") @RequestParam(required = false) String sort,
            @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        CustomPagination<TaskResponse> paginatedTasks = taskService.getAllTasks(userId, page, size, sort, title, status);
        return Response.renderJSON(paginatedTasks, "Successfully retrieved all tasks");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<?> getTaskById(@PathVariable String id, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        TaskResponse task = taskService.getTaskById(id, userId);
        return Response.renderJSON(task);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<?> updateTask(@PathVariable String id, @Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Updating task: {} for user: {}", id, user.getId());
        TaskResponse updatedTask = taskService.updateTask(id, taskRequest, user.getId());
        return Response.renderJSON(updatedTask, "Task updated successfully!");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<?> deleteTask(@PathVariable String id, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        taskService.deleteTask(id, userId);
        return Response.renderJSON(null, "Task deleted successfully!");
    }

    @PostMapping("/{taskId}/start")
    @Operation(summary = "Start a task session")
    public ResponseEntity<?> startTask(@PathVariable String taskId, @Valid @RequestBody TaskSessionRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        request.setTaskId(taskId);
        TaskSessionResponse session = taskService.startTask(request, user.getId());
        return Response.renderJSON(session, "Task session started successfully");
    }

    @PostMapping("/sessions/{sessionId}/pause")
    @Operation(summary = "Pause a task session")
    public ResponseEntity<?> pauseTask(@PathVariable String sessionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        TaskSessionResponse session = taskService.pauseTask(sessionId, user.getId());
        return Response.renderJSON(session, "Task session paused successfully");
    }

    @PostMapping("/sessions/{sessionId}/resume")
    @Operation(summary = "Resume a task session")
    public ResponseEntity<?> resumeTask(@PathVariable String sessionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        TaskSessionResponse session = taskService.resumeTask(sessionId, user.getId());
        return Response.renderJSON(session, "Task session resumed successfully");
    }

    @PostMapping("/sessions/{sessionId}/stop")
    @Operation(summary = "Stop a task session")
    public ResponseEntity<?> stopTask(@PathVariable String sessionId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        TaskSessionResponse session = taskService.stopTask(sessionId, user.getId());
        return Response.renderJSON(session, "Task session stopped successfully");
    }

    @PutMapping("/sessions/{sessionId}/notes")
    @Operation(summary = "Update task session notes")
    public ResponseEntity<?> updateTaskSessionNotes(@PathVariable String sessionId, @RequestBody String notes, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        TaskSessionResponse session = taskService.updateTaskSessionNotes(sessionId, notes, user.getId());
        return Response.renderJSON(session, "Task session notes updated successfully");
    }

    @GetMapping("/{taskId}/sessions")
    @Operation(summary = "Get task sessions", description = "Retrieves all sessions for a specific task with pagination")
    public ResponseEntity<?> getTaskSessions(
            @Parameter(description = "ID of the task") @PathVariable String taskId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CustomPagination<TaskSessionResponse> sessions = taskService.getTaskSessions(taskId, user.getId(), page, size);
        return Response.renderJSON(sessions);
    }

    @PostMapping("/sync")
    @Operation(summary = "Synchronize tasks between devices")
    public ResponseEntity<?> synchronizeTasks(@RequestBody List<TaskSyncRequest> syncRequests, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        List<TaskSyncResponse> syncResponses = taskService.synchronizeTasks(user.getId(), syncRequests);
        return Response.renderJSON(syncResponses, "Tasks synchronized successfully");
    }
}