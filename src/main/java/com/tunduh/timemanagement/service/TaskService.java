package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest, String userId);
    TaskResponse updatePhoto(MultipartFile files, String id);
    CustomPagination<TaskResponse> getAllTasks(String userId, int page, int size, String sort, String title, String status);
    TaskResponse getTaskById(String id, String userId);
    TaskResponse updateTask(String id, TaskRequest taskRequest, String userId);
    void deleteTask(String id, String userId);
}