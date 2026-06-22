package com.banking.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.banking.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(404)
                                .timestamp(LocalDateTime.now())
                                .build());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleBalance(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(400)
                                .timestamp(LocalDateTime.now())
                                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                                .message(ex.getBindingResult()
                                                .getFieldError()
                                                .getDefaultMessage()
                                )
                                .status(400)
                                .timestamp(LocalDateTime.now())
                                .build());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse
                                .builder()
                                .message(ex.getMessage())
                                .status(400)
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
    }
