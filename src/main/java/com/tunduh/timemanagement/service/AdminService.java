package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.SubmissionRequest;
import com.tunduh.timemanagement.dto.response.AnalyticsResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.SubmissionResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;

import java.util.List;

public interface AdminService {
    SubmissionResponse createSubmission(SubmissionRequest submissionRequest);
    CustomPagination<SubmissionResponse> getAllSubmissions(int page, int size, String sort, String title, String status);
    SubmissionResponse getSubmissionById(String id);
    SubmissionResponse updateSubmission(String id, SubmissionRequest submissionRequest);
    void deleteSubmission(String id);
    AnalyticsResponse getUserAnalytics();
    List<ShopItemResponse> getAllShopItems();
}