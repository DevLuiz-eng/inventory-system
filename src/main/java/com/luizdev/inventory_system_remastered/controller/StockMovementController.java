package com.luizdev.inventory_system_remastered.controller;

import com.luizdev.inventory_system_remastered.dto.request.StockMovementRequest;
import com.luizdev.inventory_system_remastered.dto.response.StockMovementResponse;
import com.luizdev.inventory_system_remastered.services.StockMovementService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/stock-movements")
public class StockMovementController {

    private final StockMovementService service;

    public StockMovementController(StockMovementService service) {
        this.service = service;
    }

    @PostMapping("/entry")
    public ResponseEntity<StockMovementResponse> entry(@RequestBody @Valid StockMovementRequest request) {
        StockMovementResponse response = service.entry(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<StockMovementResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<StockMovementResponse>> getByProductId(
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(service.getByProductId(productId, pageable));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<Page<StockMovementResponse>> getByWarehouseId(
            @PathVariable Long warehouseId,
            Pageable pageable) {
        return ResponseEntity.ok(service.getByWarehouseId(warehouseId, pageable));
    }
}