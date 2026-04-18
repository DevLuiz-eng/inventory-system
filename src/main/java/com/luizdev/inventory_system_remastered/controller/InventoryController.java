package com.luizdev.inventory_system_remastered.controller;

import com.luizdev.inventory_system_remastered.dto.response.InventoryResponse;
import com.luizdev.inventory_system_remastered.services.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getByProductId(productId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<Page<InventoryResponse>> getByWarehouseId(
            @PathVariable Long warehouseId,
            Pageable pageable) {
        return ResponseEntity.ok(service.getByWarehouseId(warehouseId, pageable));
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<InventoryResponse> getByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(service.getByProductAndWarehouse(productId, warehouseId));
    }
}