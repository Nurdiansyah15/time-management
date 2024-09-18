package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.PurchaseRequest;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.PurchaseService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Purchase management operations")
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping
    @Operation(summary = "Create a new purchase")
    public ResponseEntity<?> createPurchase(@Valid @RequestBody PurchaseRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        PurchaseResponse purchase = purchaseService.createPurchase(request, user.getId());
        return Response.renderJSON(purchase, "Purchase created successfully");
    }

    @GetMapping
    @Operation(summary = "Get user purchases")
    public ResponseEntity<?> getUserPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CustomPagination<PurchaseResponse> purchases = purchaseService.getUserPurchases(user.getId(), page, size);

        return ResponseEntity.ok(purchases);
    }

    @GetMapping("/{purchaseId}")
    @Operation(summary = "Get a specific purchase")
    public ResponseEntity<?> getPurchaseById(@PathVariable String purchaseId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        PurchaseResponse purchase = purchaseService.getPurchaseById(purchaseId, user.getId());
        return Response.renderJSON(purchase);
    }
}