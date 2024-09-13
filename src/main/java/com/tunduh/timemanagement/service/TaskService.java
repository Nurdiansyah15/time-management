package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest taskRequest, String userId);

    List<TaskResponse> getAllTasks(String userId);

    TaskResponse getTaskById(String id);

    TaskResponse updateTask(String id, TaskRequest taskRequest, String userId);

    void deleteTask(String id, String userId);
}
