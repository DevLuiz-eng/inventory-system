package com.luizdev.inventory_system_remastered.exceptions.paymentExceptions;

public class PaymentAlreadyPaidException extends RuntimeException {
    public PaymentAlreadyPaidException(String message) {
        super(message);
    }
}
