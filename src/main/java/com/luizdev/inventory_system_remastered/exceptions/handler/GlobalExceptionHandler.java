package com.luizdev.inventory_system_remastered.exceptions.handler;

import com.luizdev.inventory_system_remastered.builder.ErrorResponseBuilder;
import com.luizdev.inventory_system_remastered.dto.error.ErrorResponse;
import com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions.InsufficientStockException;
import com.luizdev.inventory_system_remastered.exceptions.inventoryExceptions.InventoryNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.paymentExceptions.PaymentAlreadyPaidException;
import com.luizdev.inventory_system_remastered.exceptions.paymentExceptions.PaymentNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.productExceptions.ProductNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.saleExceptions.SaleNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.EmailAlreadyExistsException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.userExceptions.UserNotFoundException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseAlreadyInactiveException;
import com.luizdev.inventory_system_remastered.exceptions.warehouseExceptions.WarehouseNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        log.warn("Usuário não encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Tentativa de duplicar email já cadastrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserAlreadyInactiveException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyInactive(UserAlreadyInactiveException ex, HttpServletRequest request) {
        log.warn("Usuário já está inativo: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        log.warn("Produto não encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ProductAlreadyInactiveException.class)
    public ResponseEntity<ErrorResponse> handleProductAlreadyInactive(ProductAlreadyInactiveException ex, HttpServletRequest request) {
        log.warn("Produto já está inativo: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(WarehouseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWarehouseNotFound(WarehouseNotFoundException ex, HttpServletRequest request) {
        log.warn("Armazém não encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(WarehouseAlreadyInactiveException.class)
    public ResponseEntity<ErrorResponse> handleWarehouseAlreadyInactive(WarehouseAlreadyInactiveException ex, HttpServletRequest request) {
        log.warn("Armazém já está inativo: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInventoryNotFound(InventoryNotFoundException ex, HttpServletRequest request) {
        log.warn("Inventário não encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        log.warn("Estoque insuficiente: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(SaleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSaleNotFound(SaleNotFoundException ex, HttpServletRequest request) {
        log.warn("Venda não encontrada: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(PaymentNotFoundException ex, HttpServletRequest request) {
        log.warn("Pagamento não encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(PaymentAlreadyPaidException.class)
    public ResponseEntity<ErrorResponse> handlePaymentAlreadyPaid(PaymentAlreadyPaidException ex, HttpServletRequest request) {
        log.warn("Pagamento já confirmado: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");

        log.warn("Erro de validação: {}", errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, request.getRequestURI());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno de servidor", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status)
                .body(ErrorResponseBuilder.buildError(status, message, path));
    }
}