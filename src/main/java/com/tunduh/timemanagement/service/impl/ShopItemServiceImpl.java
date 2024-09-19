package com.tunduh.timemanagement.service.impl;

import com.tunduh.timemanagement.dto.request.ShopItemRequest;
import com.tunduh.timemanagement.dto.response.PurchaseResponse;
import com.tunduh.timemanagement.dto.response.ShopItemResponse;
import com.tunduh.timemanagement.entity.ShopItemEntity;
import com.tunduh.timemanagement.entity.UserEntity;
import com.tunduh.timemanagement.entity.PurchaseEntity;
import com.tunduh.timemanagement.entity.TransactionEntity;
import com.tunduh.timemanagement.exception.InsufficientPointsException;
import com.tunduh.timemanagement.exception.ResourceNotFoundException;
import com.tunduh.timemanagement.repository.ShopItemRepository;
import com.tunduh.timemanagement.repository.UserRepository;
import com.tunduh.timemanagement.repository.PurchaseRepository;
import com.tunduh.timemanagement.service.CloudinaryService;
import com.tunduh.timemanagement.service.ShopItemService;
import com.tunduh.timemanagement.service.TransactionService;
import com.tunduh.timemanagement.utils.pagination.CustomPagination;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopItemServiceImpl implements ShopItemService {
    private final ShopItemRepository shopItemRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final CloudinaryService cloudinaryService;
    private final TransactionService transactionService;

    @Override
    @Transactional
    public ShopItemResponse createShopItem(ShopItemRequest shopItemRequest) {
        ShopItemEntity shopItem = ShopItemEntity.builder()
                .name(shopItemRequest.getName())
                .price(shopItemRequest.getPrice())
                .stock(shopItemRequest.getStock())
                .category(shopItemRequest.getCategory())
                .description(shopItemRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        ShopItemEntity savedShopItem = shopItemRepository.save(shopItem);
        return mapToShopItemResponse(savedShopItem);
    }

    @Override
    @Transactional
    public ShopItemResponse updatePhoto(MultipartFile file, String id) {
        ShopItemEntity shopItem = shopItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop item not found"));
        String url = cloudinaryService.uploadFile(file, "shop-item");
        shopItem.setItemPicture(url);
        ShopItemEntity updatedShopItem = shopItemRepository.save(shopItem);
        return mapToShopItemResponse(updatedShopItem);
    }

    @Override
    public CustomPagination<ShopItemResponse> getAllShopItems(int page, int size, String sort, String name, Integer maxPrice, ShopItemEntity.ItemCategory category) {
        Pageable pageable = createPageable(page, size, sort);
        Specification<ShopItemEntity> spec = createSpecification(name, maxPrice, category);
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
        shopItem.setPrice(shopItemRequest.getPrice());
        shopItem.setStock(shopItemRequest.getStock());
        shopItem.setCategory(shopItemRequest.getCategory());
        shopItem.setDescription(shopItemRequest.getDescription());
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

        if (shopItem.getStock() < quantity) {
            throw new IllegalStateException("Not enough stock available");
        }

        shopItem.setStock(shopItem.getStock() - quantity);
        shopItemRepository.save(shopItem);

        // Create transaction for purchase
        transactionService.createTransaction(userId, -totalPrice,
                TransactionEntity.TransactionType.PURCHASE,
                "Purchase of " + quantity + " " + shopItem.getName());

        PurchaseEntity purchase = PurchaseEntity.builder()
                .user(user)
                .shopItem(shopItem)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);
        return mapToPurchaseResponse(savedPurchase);
    }

    private ShopItemResponse mapToShopItemResponse(ShopItemEntity shopItem) {
        return ShopItemResponse.builder()
                .id(shopItem.getId())
                .name(shopItem.getName())
                .itemPicture(shopItem.getItemPicture())
                .price(shopItem.getPrice())
                .stock(shopItem.getStock())
                .category(shopItem.getCategory())
                .description(shopItem.getDescription())
                .createdAt(shopItem.getCreatedAt())
                .updatedAt(shopItem.getUpdatedAt())
                .build();
    }

    private PurchaseResponse mapToPurchaseResponse(PurchaseEntity purchase) {
        return PurchaseResponse.builder()
                .id(purchase.getId())
                .userId(purchase.getUser().getId())
                .shopItemId(purchase.getShopItem().getId())
                .shopItemName(purchase.getShopItem().getName())
                .quantity(purchase.getQuantity())
                .totalPrice(purchase.getTotalPrice())
                .createdAt(purchase.getCreatedAt())
                .build();
    }

    private Pageable createPageable(int page, int size, String sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null) {
            String[] sortParams = sort.split(",");
            for (String param : sortParams) {
                String[] keyDirection = param.split(":");
                String key = keyDirection[0];
                Sort.Direction direction = keyDirection.length > 1 &&
                        keyDirection[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(direction, key));
            }
        }
        return PageRequest.of(page, size, Sort.by(orders));
    }

    private Specification<ShopItemEntity> createSpecification(String name, Integer maxPrice, ShopItemEntity.ItemCategory category) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}