package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.service.TaskService;
import com.tunduh.timemanagement.utils.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        String userId = authentication.getName();
        TaskResponse createdTask = taskService.createTask(taskRequest, userId);
        return Response.renderJSON(createdTask, "Task created successfully!");
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(Authentication authentication) {
        String userId = authentication.getName();
        List<TaskResponse> tasks = taskService.getAllTasks(userId);
        return Response.renderJSON(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable String id) {
        TaskResponse task = taskService.getTaskById(id);
        return Response.renderJSON(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String id, @Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        String userId = authentication.getName();
        TaskResponse updatedTask = taskService.updateTask(id, taskRequest, userId);
        return Response.renderJSON(updatedTask, "Task updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id, Authentication authentication) {
        String userId = authentication.getName();
        taskService.deleteTask(id, userId);
        return Response.renderJSON(null, "Task deleted successfully!");
    }
}