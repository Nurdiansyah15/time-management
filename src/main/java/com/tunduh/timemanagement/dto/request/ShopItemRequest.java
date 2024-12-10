package com.tunduh.timemanagement.dto.request;

import com.tunduh.timemanagement.entity.ShopItemEntity.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ShopItemRequest {
    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Item price must be entered")
    @Min(value = 0, message = "Price must be non-negative")
    private Integer price;

    @NotNull(message = "Item stock must be entered")
    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;

    @NotNull(message = "Item category is required")
    private ItemCategory category;

    private String description;
}