package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.PurchaseRequest;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;

public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest request, String userId);
    CustomPagination<PurchaseResponse> getUserPurchases(String userId, int page, int size);
    PurchaseResponse getPurchaseById(String purchaseId, String userId);
    CustomPagination<PurchaseResponse> getAllPurchases(int page, int size, String sort);
}