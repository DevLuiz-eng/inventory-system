package com.luizdev.inventory_system_remastered.controller;

import com.luizdev.inventory_system_remastered.dto.request.SaleRequest;
import com.luizdev.inventory_system_remastered.dto.response.SaleResponse;
import com.luizdev.inventory_system_remastered.services.SaleService;
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
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SaleResponse> create(@RequestBody @Valid SaleRequest request) {
        SaleResponse response = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<Page<SaleResponse>> getByWarehouseId(
            @PathVariable Long warehouseId,
            Pageable pageable) {
        return ResponseEntity.ok(service.getByWarehouseId(warehouseId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<SaleResponse>> getByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(service.getByUserId(userId, pageable));
    }
}