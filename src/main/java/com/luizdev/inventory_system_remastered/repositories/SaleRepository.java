package com.luizdev.inventory_system_remastered.repositories;

import com.luizdev.inventory_system_remastered.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByWarehouseId(Long warehouseId);
    Page<Sale> findByWarehouseId(Long warehouseId, Pageable pageable);

    List<Sale> findByCreatedById(Long createdById);
    Page<Sale> findByCreatedById(Long createdById, Pageable pageable);

    List<Sale> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}