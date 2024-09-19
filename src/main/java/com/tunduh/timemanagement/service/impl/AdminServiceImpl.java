package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.AnalyticsResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.AdminService;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.ShopItemService;
import com.tunduh.timemanagement.service.PurchaseService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final ShopItemService shopItemService;
    private final PurchaseService purchaseService;
    private final CloudinaryService cloudinaryService;

    @Override
    public CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice) {
        return shopItemService.getAllShopItems(page, size, sort, name, maxPrice, null);
    }

    @Override
    public CustomPagination<PurchaseResponse> getAllPurchases(int page, int size, String sort) {
        return purchaseService.getAllPurchases(page, size, sort);
    }

//    @Override
//    public AnalyticsResponse getUserAnalytics() {
//        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
//        return AnalyticsResponse.builder()
//                .totalUsers(userRepository.count())
//                .activeUsers(userRepository.countActiveUsers(thirtyDaysAgo))
//                .averageTasksPerUser(userRepository.getAverageTasksPerUser())
//                .build();
//    }
}