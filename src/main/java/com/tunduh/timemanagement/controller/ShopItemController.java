package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.request.PurchaseRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.service.ShopItemService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop-items")
@RequiredArgsConstructor
@Tag(name = "Shop", description = "Shop operations")
public class ShopItemController {
    private final ShopItemService shopItemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new shop item")
    public ResponseEntity<?> createShopItem(@Valid @RequestBody ShopItemRequest shopItemRequest) {
        ShopItemResponse createdShopItem = shopItemService.createShopItem(shopItemRequest);
        return Response.renderJSON(createdShopItem, "Shop item created successfully!");
    }

    @GetMapping
    @Operation(summary = "Get all shop items with pagination and filtering")
    public ResponseEntity<?> getAllShopItems(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort parameter (e.g., 'name,asc' or 'price,desc')") @RequestParam(required = false) String sort,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by max price") @RequestParam(required = false) Integer maxPrice) {
        CustomPagination<ShopItemResponse> shopItems = shopItemService.getAllShopItems(page, size, sort, name, maxPrice);
        return Response.renderJSON(shopItems);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a shop item by ID")
    public ResponseEntity<?> getShopItemById(@PathVariable String id) {
        ShopItemResponse shopItem = shopItemService.getShopItemById(id);
        return Response.renderJSON(shopItem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a shop item")
    public ResponseEntity<?> updateShopItem(@PathVariable String id, @Valid @RequestBody ShopItemRequest shopItemRequest) {
        ShopItemResponse updatedShopItem = shopItemService.updateShopItem(id, shopItemRequest);
        return Response.renderJSON(updatedShopItem, "Shop item updated successfully!");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a shop item")
    public ResponseEntity<?> deleteShopItem(@PathVariable String id) {
        shopItemService.deleteShopItem(id);
        return Response.renderJSON(null, "Shop item deleted successfully!");
    }

    @PostMapping("/{id}/purchase")
    @Operation(summary = "Purchase a shop item")
    public ResponseEntity<?> purchaseItem(@PathVariable String id, @Valid @RequestBody PurchaseRequest purchaseRequest, Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();
        String userId = principal.getId();
        PurchaseResponse purchaseResponse = shopItemService.purchaseItem(id, purchaseRequest.getQuantity(), userId);
        return Response.renderJSON(purchaseResponse, "Item purchased successfully!");
    }
}