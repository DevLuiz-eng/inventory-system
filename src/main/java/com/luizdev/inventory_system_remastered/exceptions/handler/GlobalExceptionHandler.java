package com.luizdev.inventory_system_remastered.exceptions.handler;

import com.luizdev.inventory_system_remastered.dto.error.ErrorResponse;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.EmailAlreadyExistsException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import com.luizdev.inventory_system_remastered.builder.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {

        log.warn("Usuário não encontrado: {}", ex.getMessage());

        return ResponseEntity.
                status(HttpStatus.NOT_FOUND).body(ErrorResponseBuilder.buildError(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest request) {

        log.warn("Tentativa de duplicar email já cadastrado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseBuilder.buildError(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(UserAlreadyInactiveException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyInactive(UserAlreadyInactiveException ex, HttpServletRequest request) {

        log.warn("Usuário já está inativo: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponseBuilder.buildError(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex, HttpServletRequest request) {

        log.error("erro inesperado", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponseBuilder.buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno de servidor",
                        request.getRequestURI()));
    }
}
