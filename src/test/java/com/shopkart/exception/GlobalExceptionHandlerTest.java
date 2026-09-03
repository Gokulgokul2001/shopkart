package com.shopkart.exception;

import com.shopkart.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.mockito.Mockito.*;

import jakarta.validation.Path;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleProductNotFound() {

        ProductNotFoundException exception =
                new ProductNotFoundException(
                        "Product not found with id: 999");

        ResponseEntity<ApiResponse<Void>> response =
                handler.handleProductNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(404, response.getBody().getStatus());
        assertEquals(
                "Product not found with id: 999",
                response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void handleCategoryNotFound() {

        CategoryNotFoundException exception =
                new CategoryNotFoundException(
                        "Category not found with id: 999");

        ResponseEntity<ApiResponse<Void>> response =
                handler.handleCategoryNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(404, response.getBody().getStatus());
        assertEquals(
                "Category not found with id: 999",
                response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void handleCategoryInUseException() {

        CategoryInUseException exception =
                new CategoryInUseException(
                        "Category cannot be deleted because it is in use");

        ResponseEntity<ApiResponse<Void>> response =
                handler.handleCategoryInUseException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(409, response.getBody().getStatus());
        assertEquals(
                "Category cannot be deleted because it is in use",
                response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void handleIllegalArgumentException() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Minimum price cannot be greater than maximum price");

        ResponseEntity<ApiResponse<Void>> response =
                handler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(400, response.getBody().getStatus());
        assertEquals(
                "Minimum price cannot be greater than maximum price",
                response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void handleConstraintViolation() {

        ConstraintViolation<?> violation =
                mock(ConstraintViolation.class);

        Path path = mock(Path.class);

        when(path.toString()).thenReturn("getAllProducts.page");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage())
                .thenReturn("Page number cannot be negative");

        ConstraintViolationException exception =
                new ConstraintViolationException(
                        Set.of(violation));

        ResponseEntity<ApiResponse<Map<String, String>>> response =
                handler.handleConstraintViolation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation failed",
                response.getBody().getMessage());

        assertNotNull(response.getBody().getData());

        assertEquals(
                "Page number cannot be negative",
                response.getBody().getData()
                        .get("getAllProducts.page"));
    }
    @Test
    void handleValidationErrors() {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError =
                new FieldError(
                        "productRequest",
                        "name",
                        "Name is required");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        ResponseEntity<ApiResponse<Map<String, String>>> response =
                handler.handleValidationErrors(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(400, response.getBody().getStatus());
        assertEquals(
                "Validation failed",
                response.getBody().getMessage());

        assertNotNull(response.getBody().getData());

        assertEquals(
                "Name is required",
                response.getBody().getData().get("name"));
    }
}