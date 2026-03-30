package com.luizdev.inventory_system_remastered.exceptions.handler;

import com.luizdev.inventory_system_remastered.exceptions.userExceptions.EmailAlreadyExistsException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex){

        log.warn("Usuário não encontrado: {}", ex.getMessage());

        return ResponseEntity.
                status(HttpStatus.NOT_FOUND).
                body(ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException ex) {

        log.warn("Tentativa de duplicar email já cadastrado: {}", ex.getMessage());

        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST).
                body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyInactiveException.class)
    public ResponseEntity<String> handleAlreadyInactive(UserAlreadyInactiveException ex) {

        log.warn("Usuário já está inativo: {}", ex.getMessage());

        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST).
                body(ex.getMessage());
    }
}
