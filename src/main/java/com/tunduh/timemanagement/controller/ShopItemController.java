package com.tunduh.timemanagement.controller;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.service.ShopItemService;
import com.tunduh.timemanagement.utils.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop-items")
@RequiredArgsConstructor
public class ShopItemController {

    private final ShopItemService shopItemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createShopItem(@Valid @RequestBody ShopItemRequest shopItemRequest) {
        ShopItemResponse createdShopItem = shopItemService.createShopItem(shopItemRequest);
        return Response.renderJSON(createdShopItem, "Shop item created successfully!");
    }

    @GetMapping
    public ResponseEntity<?> getAllShopItems() {
        List<ShopItemResponse> shopItems = shopItemService.getAllShopItems();
        return Response.renderJSON(shopItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShopItemById(@PathVariable String id) {
        ShopItemResponse shopItem = shopItemService.getShopItemById(id);
        return Response.renderJSON(shopItem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShopItem(@PathVariable String id, @Valid @RequestBody ShopItemRequest shopItemRequest) {
        ShopItemResponse updatedShopItem = shopItemService.updateShopItem(id, shopItemRequest);
        return Response.renderJSON(updatedShopItem, "Shop item updated successfully!");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteShopItem(@PathVariable String id) {
        shopItemService.deleteShopItem(id);
        return Response.renderJSON(null, "Shop item deleted successfully!");
    }
}