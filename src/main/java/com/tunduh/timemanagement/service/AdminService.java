package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.SubmissionRequest;
import com.tunduh.timemanagement.dto.response.AnalyticsResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.SubmissionResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice);
    CustomPagination<PurchaseResponse> getAllPurchases(int page, int size, String sort);
    //    AnalyticsResponse getUserAnalytics();
}