package com.luizdev.inventory_system_remastered.mapper;

import com.luizdev.inventory_system_remastered.dto.response.PaymentResponse;
import com.luizdev.inventory_system_remastered.entity.Payment;

public class PaymentMapper {

    public static PaymentResponse toResponse(Payment payment) {
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