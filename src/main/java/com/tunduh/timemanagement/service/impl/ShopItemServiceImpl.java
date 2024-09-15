package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.entity.ShopItemEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.UserTransactionEntity;
import com.tunduh.timemanagement.exception.InsufficientPointsException;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.ShopItemRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.repository.UserTransactionRepository;
import com.tunduh.timemanagement.service.ShopItemService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import com.tunduh.timemanagement.utils.specification.ShopItemSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopItemServiceImpl implements ShopItemService {
    private final ShopItemRepository shopItemRepository;
    private final UserRepository userRepository;
    private final UserTransactionRepository userTransactionRepository;

    @Override
    @Transactional
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
    public CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice) {
        Pageable pageable = createPageable(page, size, sort);
        Specification<ShopItemEntity> spec = ShopItemSpecification.getSpecification(name, maxPrice);

        Page<ShopItemEntity> shopItemPage = shopItemRepository.findAll(spec, pageable);
        return new CustomPagination<>(shopItemPage.map(this::mapToShopItemResponse));
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

    @Override
    @Transactional
    public PurchaseResponse purchaseItem(String itemId, int quantity, String userId) {
        ShopItemEntity shopItem = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int totalPrice = shopItem.getPrice() * quantity;

        if (user.getUserPoint() < totalPrice) {
            throw new InsufficientPointsException("User does not have enough points to make this purchase");
        }

        user.setUserPoint(user.getUserPoint() - totalPrice);
        userRepository.save(user);

        UserTransactionEntity transaction = UserTransactionEntity.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .shopItem(shopItem)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .transactionDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userTransactionRepository.save(transaction);

        return PurchaseResponse.builder()
                .transactionId(transaction.getId())
                .itemName(shopItem.getName())
                .quantity(quantity)
                .totalPrice(totalPrice)
                .purchaseDate(transaction.getTransactionDate())
                .build();
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

    private Pageable createPageable(int page, int size, String sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            for (String param : sortParams) {
                String[] keyDirection = param.split(":");
                String key = keyDirection[0];
                Sort.Direction direction = keyDirection.length > 1 && keyDirection[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, key));
            }
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }
}