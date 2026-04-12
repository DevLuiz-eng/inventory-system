package com.luizdev.inventory_system_remastered.repositories;

import com.luizdev.inventory_system_remastered.entity.StockMovement;
import com.luizdev.inventory_system_remastered.enums.TypeOfStockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductId(Long productId);
    Page<StockMovement> findByProductId(Long productId, Pageable pageable);

    List<StockMovement> findByWarehouseId(Long warehouseId);
    Page<StockMovement> findByWarehouseId(Long warehouseId, Pageable pageable);

    List<StockMovement> findByType(TypeOfStockMovement type);
    Page<StockMovement> findByType(TypeOfStockMovement type, Pageable pageable);

    List<StockMovement> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}