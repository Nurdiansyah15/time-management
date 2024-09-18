package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.entity.ShopItemEntity.ItemCategory;
import org.springframework.web.multipart.MultipartFile;

public interface ShopItemService {
    ShopItemResponse createShopItem(ShopItemRequest shopItemRequest);
    ShopItemResponse updatePhoto(MultipartFile file, String id);
    CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice, ItemCategory category);
    ShopItemResponse getShopItemById(String id);
    ShopItemResponse updateShopItem(String id, ShopItemRequest shopItemRequest);
    void deleteShopItem(String id);
    PurchaseResponse purchaseItem(String itemId, int quantity, String userId);
}