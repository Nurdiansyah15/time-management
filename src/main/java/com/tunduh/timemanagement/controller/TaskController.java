package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.TaskService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        TaskResponse createdTask = taskService.createTask(taskRequest, userId);
        return Response.renderJSON(createdTask, "Task created successfully!");
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {

        log.debug("getAllTasks called with page: {}, size: {}, sort: {}, title: {}, status: {}",
                page, size, sort, title, status);

        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        log.debug("User ID from authentication: {}", userId);

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        log.debug("Calling taskService.getAllTasks");
        CustomPagination<TaskResponse> paginatedTasks = taskService.getAllTasks(userId, pageable, title, status);

        log.debug("Returning response with {} items", paginatedTasks.getContent().size());
        return Response.renderJSON(paginatedTasks,"Successfully retrieved all tasks");
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            log.debug("No sort parameter provided, using unsorted");
            return Sort.unsorted();
        }

        log.debug("Parsing sort parameter: {}", sort);
        String[] sortParams = sort.split(",");
        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            String[] parts = param.trim().split(":");
            String property = parts[0];
            Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, property));
            log.debug("Added sort order: {} {}", property, direction);
        }
        return Sort.by(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable String id) {
        TaskResponse task = taskService.getTaskById(id);
        return Response.renderJSON(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String id, @Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        TaskResponse updatedTask = taskService.updateTask(id, taskRequest, userId);
        return Response.renderJSON(updatedTask, "Task updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        taskService.deleteTask(id, userId);
        return Response.renderJSON(null, "Task deleted successfully!");
    }
}