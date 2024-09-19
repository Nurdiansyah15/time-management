package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.SubmissionRequest;
import com.tunduh.timemanagement.dto.response.AnalyticsResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.SubmissionResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    SubmissionResponse createSubmission(SubmissionRequest submissionRequest);
    SubmissionResponse updatePhoto(MultipartFile files, String id);
    CustomPagination<SubmissionResponse> getAllSubmissions(int page, int size, String sort, String title, String status);
    CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice);
    CustomPagination<PurchaseResponse> getAllPurchases(int page, int size, String sort);
    SubmissionResponse getSubmissionById(String id);
    SubmissionResponse updateSubmission(String id, SubmissionRequest submissionRequest);
    void deleteSubmission(String id);
    AnalyticsResponse getUserAnalytics();
}