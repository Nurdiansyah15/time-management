package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;

public interface AdminService {
    CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice);
    CustomPagination<PurchaseResponse> getAllPurchases(int page, int size, String sort);
    //    AnalyticsResponse getUserAnalytics();
}