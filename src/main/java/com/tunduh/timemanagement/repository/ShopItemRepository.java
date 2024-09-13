package com.tunduh.timemanagement.repository;

import com.tunduh.timemanagement.entity.ShopItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItemEntity, String> {
}