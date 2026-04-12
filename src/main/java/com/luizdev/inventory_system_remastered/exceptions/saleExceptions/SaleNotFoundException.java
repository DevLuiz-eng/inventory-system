package com.luizdev.inventory_system_remastered.exceptions.saleExceptions;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException(String message) {
        super(message);
    }
}
