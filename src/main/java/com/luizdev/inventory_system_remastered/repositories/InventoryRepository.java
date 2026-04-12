package com.luizdev.inventory_system_remastered.repositories;

import com.luizdev.inventory_system_remastered.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    Page<Inventory> findByWarehouseId(Long warehouseId, Pageable pageable);

    List<Inventory> findByWarehouseId(Long warehouseId);

    List<Inventory> findByProductId(Long productId);
}