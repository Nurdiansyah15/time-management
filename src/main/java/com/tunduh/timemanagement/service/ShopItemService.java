package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;

public interface ShopItemService {
    ShopItemResponse createShopItem(ShopItemRequest shopItemRequest);
    CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice);
    ShopItemResponse getShopItemById(String id);
    ShopItemResponse updateShopItem(String id, ShopItemRequest shopItemRequest);
    void deleteShopItem(String id);
    PurchaseResponse purchaseItem(String itemId, int quantity, String userId);
}