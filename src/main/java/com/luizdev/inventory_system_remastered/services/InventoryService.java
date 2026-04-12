package com.luizdev.inventory_system_remastered.services;

import com.luizdev.inventory_system_remastered.dto.response.InventoryResponse;
import com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions.InventoryNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import com.luizdev.inventory_system_remastered.mapper.InventoryMapper;
import com.luizdev.inventory_system_remastered.repositories.InventoryRepository;
import com.luizdev.inventory_system_remastered.repositories.ProductRepository;
import com.luizdev.inventory_system_remastered.repositories.WarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            WarehouseRepository warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<InventoryResponse> getByProductId(Long productId) {
        log.info("Buscando inventário por produto ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.warn("Produto não encontrado. ID: {}", productId);
            throw new ProductNotFoundException("Produto não encontrado: " + productId);
        }

        List<InventoryResponse> inventories = inventoryRepository.findByProductId(productId)
                .stream()
                .map(InventoryMapper::toResponse)
                .toList();

        log.info("Total de inventários encontrados para produto {}: {}", productId, inventories.size());
        return inventories;
    }

    public Page<InventoryResponse> getByWarehouseId(Long warehouseId, Pageable pageable) {
        log.info("Buscando inventário por armazém ID: {}", warehouseId);

        if (!warehouseRepository.existsById(warehouseId)) {
            log.warn("Armazém não encontrado. ID: {}", warehouseId);
            throw new WarehouseNotFoundException("Armazém não encontrado: " + warehouseId);
        }

        Page<InventoryResponse> inventories = inventoryRepository.findByWarehouseId(warehouseId, pageable)
                .map(InventoryMapper::toResponse);

        log.info("Total de inventários encontrados para armazém {}: {}", warehouseId, inventories.getTotalElements());
        return inventories;
    }

    public InventoryResponse getByProductAndWarehouse(Long productId, Long warehouseId) {
        log.info("Buscando inventário. Produto ID: {}, Armazém ID: {}", productId, warehouseId);

        if (!productRepository.existsById(productId)) {
            log.warn("Produto não encontrado. ID: {}", productId);
            throw new ProductNotFoundException("Produto não encontrado: " + productId);
        }

        if (!warehouseRepository.existsById(warehouseId)) {
            log.warn("Armazém não encontrado. ID: {}", warehouseId);
            throw new WarehouseNotFoundException("Armazém não encontrado: " + warehouseId);
        }

        return inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .map(InventoryMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Inventário não encontrado. Produto ID: {}, Armazém ID: {}", productId, warehouseId);
                    return new InventoryNotFoundException(
                            "Inventário não encontrado para o produto: " + productId +
                                    " no armazém: " + warehouseId);
                });
    }
}