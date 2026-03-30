package com.luizdev.inventory_system_remastered.exceptions.userExceptions;

public class UserAlreadyInactiveException extends RuntimeException {
    public UserAlreadyInactiveException(String message) {
        super(message);
    }
}
