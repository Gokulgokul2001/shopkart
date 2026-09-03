package com.shopkart.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than zero")
    private Double price;

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Product quantity cannot be negative")
    private Integer quantity;

    private Long categoryId;

    public @NotBlank(message = "Product name is required") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Product name is required") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Product description is required") String getDescription() {
        return description;
    }

    public void setDescription(@NotBlank(message = "Product description is required") String description) {
        this.description = description;
    }

    public @NotNull(message = "Product price is required") @DecimalMin(value = "0.01", message = "Product price must be greater than zero") Double getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Product price is required") @DecimalMin(value = "0.01", message = "Product price must be greater than zero") Double price) {
        this.price = price;
    }

    public @NotNull(message = "Product quantity is required") @Min(value = 0, message = "Product quantity cannot be negative") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "Product quantity is required") @Min(value = 0, message = "Product quantity cannot be negative") Integer quantity) {
        this.quantity = quantity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

