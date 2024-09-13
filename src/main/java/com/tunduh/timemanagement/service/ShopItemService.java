package com.tunduh.timemanagement.service;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;

import java.util.List;

public interface ShopItemService {

    ShopItemResponse createShopItem(ShopItemRequest shopItemRequest);

    List<ShopItemResponse> getAllShopItems();

    ShopItemResponse getShopItemById(String id);

    ShopItemResponse updateShopItem(String id, ShopItemRequest shopItemRequest);

    void deleteShopItem(String id);
}
