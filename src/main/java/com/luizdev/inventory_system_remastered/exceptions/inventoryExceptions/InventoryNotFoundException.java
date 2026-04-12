package com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}
