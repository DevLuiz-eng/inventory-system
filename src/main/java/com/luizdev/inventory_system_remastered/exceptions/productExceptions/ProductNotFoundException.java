package com.luizdev.inventory_system_remastered.exceptions.productExceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
