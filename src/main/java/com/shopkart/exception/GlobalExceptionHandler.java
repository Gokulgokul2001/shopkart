package com.shopkart.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<com.shopkart.response.ApiResponse<Map<String, String>>>
    handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new com.shopkart.response.ApiResponse<>(
                        400,
                        "Validation failed",
                        errors
                ));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<com.shopkart.response.ApiResponse<Void>>
    handleProductNotFound(ProductNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new com.shopkart.response.ApiResponse<>(
                        404,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<com.shopkart.response.ApiResponse<Void>>
    handleCategoryNotFound(CategoryNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new com.shopkart.response.ApiResponse<>(
                        404,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<com.shopkart.response.ApiResponse<Void>>
    handleCategoryInUseException(CategoryInUseException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new com.shopkart.response.ApiResponse<>(
                        409,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<com.shopkart.response.ApiResponse<Map<String, String>>>
    handleConstraintViolation(ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(error ->
                errors.put(
                        error.getPropertyPath().toString(),
                        error.getMessage()
                )
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new com.shopkart.response.ApiResponse<>(
                        400,
                        "Validation failed",
                        errors
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.shopkart.response.ApiResponse<Void>>
    handleIllegalArgumentException(IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new com.shopkart.response.ApiResponse<>(
                        400,
                        ex.getMessage(),
                        null
                ));
    }
}