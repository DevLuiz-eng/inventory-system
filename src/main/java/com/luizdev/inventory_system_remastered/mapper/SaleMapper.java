package com.luizdev.inventory_system_remastered.mapper;

import com.luizdev.inventory_system_remastered.dto.response.PaymentResponse;
import com.luizdev.inventory_system_remastered.dto.response.SaleItemResponse;
import com.luizdev.inventory_system_remastered.dto.response.SaleResponse;
import com.luizdev.inventory_system_remastered.entity.Payment;
import com.luizdev.inventory_system_remastered.entity.Sale;
import com.luizdev.inventory_system_remastered.entity.SaleItem;

import java.util.List;

public class SaleMapper {

    public static SaleResponse toResponse(Sale sale, List<Payment> payments) {
        return new SaleResponse(
                sale.getId(),
                sale.getWarehouse().getId(),
                sale.getWarehouse().getName(),
                sale.getCreatedBy().getId(),
                sale.getCreatedBy().getName(),
                sale.getTotalAmount(),
                toSaleItemResponseList(sale.getSaleItems()),
                toPaymentResponseList(payments),
                sale.getCreatedAt()
        );
    }

    private static List<SaleItemResponse> toSaleItemResponseList(List<SaleItem> items) {
        if (items == null) return List.of();
        return items.stream()
                .map(SaleMapper::toSaleItemResponse)
                .toList();
    }

    private static SaleItemResponse toSaleItemResponse(SaleItem item) {
        return new SaleItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }

    private static List<PaymentResponse> toPaymentResponseList(List<Payment> payments) {
        if (payments == null) return List.of();
        return payments.stream()
                .map(SaleMapper::toPaymentResponse)
                .toList();
    }

    private static PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getDueDate(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}