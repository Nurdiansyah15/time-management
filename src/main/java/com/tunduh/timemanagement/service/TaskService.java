package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.TaskRequest;
import com.tunduh.timemanagement.dto.request.TaskSessionRequest;
import com.tunduh.timemanagement.dto.request.TaskSyncRequest;
import com.tunduh.timemanagement.dto.response.TaskResponse;
import com.tunduh.timemanagement.dto.response.TaskSessionResponse;
import com.tunduh.timemanagement.dto.response.TaskSyncResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest taskRequest, String userId);
    TaskResponse updatePhoto(MultipartFile file, String id);
    CustomPagination<TaskResponse> getAllTasks(String userId, int page, int size, String sort, String title, String status);
    TaskResponse getTaskById(String id, String userId);
    TaskResponse updateTask(String id, TaskRequest taskRequest, String userId);
    void deleteTask(String id, String userId);
    void generateRecurringTasks();
    TaskSessionResponse startTask(TaskSessionRequest request, String userId);
    TaskSessionResponse pauseTask(String sessionId, String userId);
    TaskSessionResponse resumeTask(String sessionId, String userId);
    TaskSessionResponse stopTask(String sessionId, String userId);
    TaskSessionResponse updateTaskSessionNotes(String sessionId, String notes, String userId);
    CustomPagination<TaskSessionResponse> getTaskSessions(String taskId, String userId, int page, int size);
    List<TaskSyncResponse> synchronizeTasks(String userId, List<TaskSyncRequest> syncRequests);
}