package com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
