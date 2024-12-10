package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.PurchaseRequest;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folderName);

    interface PurchaseService {
        PurchaseResponse createPurchase(PurchaseRequest request, String userId);
        CustomPagination<PurchaseResponse> getUserPurchases(String userId, int page, int size);
        PurchaseResponse getPurchaseById(String purchaseId, String userId);
    }
}