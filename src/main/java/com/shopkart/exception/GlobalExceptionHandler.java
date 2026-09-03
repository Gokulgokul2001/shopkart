package com.shopkart.exception;

import com.shopkart.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // VALIDATION ERRORS
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>>
    handleValidationErrors(
            MethodArgumentNotValidException ex) {

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
                .body(
                        new ApiResponse<>(
                                400,
                                "Validation failed",
                                errors
                        )
                );
    }

    // =========================================================
    // PRODUCT NOT FOUND
    // =========================================================

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleProductNotFound(
            ProductNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                404,
                                ex.getMessage(),
                                null
                        )
                );
    }

    // =========================================================
    // CATEGORY NOT FOUND
    // =========================================================

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleCategoryNotFound(
            CategoryNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                404,
                                ex.getMessage(),
                                null
                        )
                );
    }

    // =========================================================
    // CATEGORY IN USE
    // =========================================================

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleCategoryInUseException(
            CategoryInUseException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ApiResponse<>(
                                409,
                                ex.getMessage(),
                                null
                        )
                );
    }

    // =========================================================
    // CONSTRAINT VIOLATION
    // =========================================================

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>>
    handleConstraintViolation(
            ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations()
                .forEach(error ->
                        errors.put(
                                error.getPropertyPath().toString(),
                                error.getMessage()
                        )
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ApiResponse<>(
                                400,
                                "Validation failed",
                                errors
                        )
                );
    }

    // =========================================================
    // ILLEGAL ARGUMENT
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleIllegalArgumentException(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ApiResponse<>(
                                400,
                                ex.getMessage(),
                                null
                        )
                );
    }
}