package com.luizdev.inventory_system_remastered.builder;

import com.luizdev.inventory_system_remastered.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponseBuilder {


    public static ErrorResponse buildError(HttpStatus status, String message, String path) {

        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path

        );

    }
}
