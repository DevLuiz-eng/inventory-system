package com.luizdev.inventory_system_remastered.controller;

import com.luizdev.inventory_system_remastered.dto.response.PaymentResponse;
import com.luizdev.inventory_system_remastered.enums.PaymentMethod;
import com.luizdev.inventory_system_remastered.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<PaymentResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirm(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<PaymentResponse>> getBySaleId(@PathVariable Long saleId) {
        return ResponseEntity.ok(service.getBySaleId(saleId));
    }

    @GetMapping("/method/{method}")
    public ResponseEntity<Page<PaymentResponse>> getByMethod(
            @PathVariable PaymentMethod method,
            Pageable pageable) {
        return ResponseEntity.ok(service.getByMethod(method, pageable));
    }
}