package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
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
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        TaskResponse createdTask = taskService.createTask(taskRequest, userId);
        return Response.renderJSON(createdTask, "Task created successfully!");
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
    @Operation(summary = "Update a task")
    public ResponseEntity<?> updateTask(@PathVariable String id, @Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        TaskResponse updatedTask = taskService.updateTask(id, taskRequest, userId);
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
}