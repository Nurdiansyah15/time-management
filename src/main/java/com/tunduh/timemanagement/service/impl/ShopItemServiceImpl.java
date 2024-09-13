package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.entity.ShopItemEntity;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.ShopItemRepository;
import com.tunduh.timemanagement.service.ShopItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopItemServiceImpl implements ShopItemService {

    private final ShopItemRepository shopItemRepository;

    @Override
    public ShopItemResponse createShopItem(ShopItemRequest shopItemRequest) {
        ShopItemEntity shopItem = ShopItemEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(shopItemRequest.getName())
                .itemPicture(shopItemRequest.getItemPicture())
                .price(shopItemRequest.getPrice())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ShopItemEntity savedShopItem = shopItemRepository.save(shopItem);
        return mapToShopItemResponse(savedShopItem);
    }

    @Override
    public List<ShopItemResponse> getAllShopItems() {
        List<ShopItemEntity> shopItems = shopItemRepository.findAll();
        return shopItems.stream()
                .map(this::mapToShopItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ShopItemResponse getShopItemById(String id) {
        ShopItemEntity shopItem = shopItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));
        return mapToShopItemResponse(shopItem);
    }

    @Override
    @Transactional
    public ShopItemResponse updateShopItem(String id, ShopItemRequest shopItemRequest) {
        ShopItemEntity shopItem = shopItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));

        shopItem.setName(shopItemRequest.getName());
        shopItem.setItemPicture(shopItemRequest.getItemPicture());
        shopItem.setPrice(shopItemRequest.getPrice());
        shopItem.setUpdatedAt(LocalDateTime.now());

        ShopItemEntity updatedShopItem = shopItemRepository.save(shopItem);
        return mapToShopItemResponse(updatedShopItem);
    }

    @Override
    @Transactional
    public void deleteShopItem(String id) {
        ShopItemEntity shopItem = shopItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));
        shopItemRepository.delete(shopItem);
    }

    private ShopItemResponse mapToShopItemResponse(ShopItemEntity shopItem) {
        return ShopItemResponse.builder()
                .id(shopItem.getId())
                .name(shopItem.getName())
                .itemPicture(shopItem.getItemPicture())
                .price(shopItem.getPrice())
                .createdAt(shopItem.getCreatedAt())
                .updatedAt(shopItem.getUpdatedAt())
                .build();
    }
}
