package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.PurchaseRequest;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.entity.PurchaseEntity;
import com.tunduh.timemanagement.entity.ShopItemEntity;
import com.tunduh.timemanagement.entity.TransactionEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.exception.InsufficientPointsException;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.PurchaseRepository;
import com.tunduh.timemanagement.repository.ShopItemRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.service.PurchaseService;
import com.tunduh.timemanagement.service.TransactionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ShopItemRepository shopItemRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ShopItemEntity shopItem = shopItemRepository.findById(request.getShopItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));

        int totalPrice = shopItem.getPrice() * request.getQuantity();
        if (user.getUserPoint() < totalPrice) {
            throw new InsufficientPointsException("User does not have enough points for this purchase");
        }

        PurchaseEntity purchase = PurchaseEntity.builder()
                .user(user)
                .shopItem(shopItem)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .build();

        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);

        // Update user points
        user.setUserPoint(user.getUserPoint() - totalPrice);
        userRepository.save(user);

        // Create transaction record
        transactionService.createTransaction(userId, -totalPrice, TransactionEntity.TransactionType.PURCHASE,
                "Purchase of " + request.getQuantity() + " " + shopItem.getName());

        return mapToPurchaseResponse(savedPurchase);
    }

    @Override
    public CustomPagination<PurchaseResponse> getUserPurchases(String userId, int page, int size) {
        Page<PurchaseEntity> purchasePage = purchaseRepository.findByUserId(userId, PageRequest.of(page, size));
        return new CustomPagination<>(purchasePage.map(this::mapToPurchaseResponse));
    }

    @Override
    public PurchaseResponse getPurchaseById(String purchaseId, String userId) {
        PurchaseEntity purchase = purchaseRepository.findByIdAndUserId(purchaseId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));
        return mapToPurchaseResponse(purchase);
    }

    private PurchaseResponse mapToPurchaseResponse(PurchaseEntity purchase) {
        PurchaseResponse response = new PurchaseResponse();
        response.setId(purchase.getId());
        response.setUserId(purchase.getUser().getId());
        response.setShopItemId(purchase.getShopItem().getId());
        response.setShopItemName(purchase.getShopItem().getName());
        response.setQuantity(purchase.getQuantity());
        response.setTotalPrice(purchase.getTotalPrice());
        response.setCreatedAt(purchase.getCreatedAt());
        return response;
    }
}