package com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions;

public class WarehouseNotFoundException extends RuntimeException {
    public WarehouseNotFoundException(String message) {
        super(message);
    }
}
