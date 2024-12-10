package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.UserResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.UserMissionEntity;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.AdminService;
import com.tunduh.timemanagement.service.PurchaseService;
import com.tunduh.timemanagement.service.ShopItemService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ShopItemService shopItemService;
    private final PurchaseService purchaseService;
    private final UserRepository userRepository;


    @Override
    public CustomPagination<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserEntity> userPage = userRepository.findAll(pageable);
        return new CustomPagination<>(userPage.map(this::mapToUserResponse));
    }

    private UserResponse mapToUserResponse(UserEntity user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setUserPoint(user.getUserPoint());
        dto.setResetTime(user.getResetTime());
        dto.setRole(user.getRole());
        dto.setUserPoint(user.getUserPoint());
        dto.setResetTime(user.getResetTime());
        return dto;
    }

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