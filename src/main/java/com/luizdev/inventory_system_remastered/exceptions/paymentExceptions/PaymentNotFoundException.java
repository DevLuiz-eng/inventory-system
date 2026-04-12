package com.luizdev.inventory_system_remastered.exceptions.paymentExceptions;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
