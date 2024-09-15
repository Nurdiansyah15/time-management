package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest, String userId);
    CustomPagination<TaskResponse> getAllTasks(String userId, Pageable pageable, String title, String status);
    TaskResponse getTaskById(String id);
    TaskResponse updateTask(String id, TaskRequest taskRequest, String userId);
    void deleteTask(String id, String userId);
}