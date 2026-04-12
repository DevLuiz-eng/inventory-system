package com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions;

public class WarehouseAlreadyInactiveException extends RuntimeException {
    public WarehouseAlreadyInactiveException(String message) {
        super(message);
    }
}
