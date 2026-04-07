package com.luizdev.inventory_system_remastered.exceptions.productExceptions;

public class ProductAlreadyInactiveException extends RuntimeException {
    public ProductAlreadyInactiveException(String message) {
        super(message);
    }
}
