package com.luizdev.inventory_system_remastered.controller;

import com.luizdev.inventory_system_remastered.dto.request.WarehouseRequest;
import com.luizdev.inventory_system_remastered.dto.response.WarehouseResponse;
import com.luizdev.inventory_system_remastered.services.WarehouseService;
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
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WarehouseResponse> create(@RequestBody @Valid WarehouseRequest request) {
        WarehouseResponse response = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<WarehouseResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<WarehouseResponse>> getAllActive(Pageable pageable) {
        return ResponseEntity.ok(service.getAllActive(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid WarehouseRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}