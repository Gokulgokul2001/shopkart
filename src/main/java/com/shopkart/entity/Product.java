package com.shopkart.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Unique identifier of the product",
            example = "1"
    )
    private Long id;

    @NotBlank(message = "Product name is required")
    @Schema(
            description = "Name of the product",
            example = "HP Pavilion"
    )
    private String name;

    @NotBlank(message = "Product description is required")
    @Schema(
            description = "Description of the product",
            example = "15-inch laptop with Intel Core i5 processor"
    )
    private String description;

    @NotNull(message = "Product price is required")
    @Positive(message = "Product price must be greater than zero")
    @Schema(
            description = "Price of the product",
            example = "60000.0"
    )
    private Double price;

    @NotNull(message = "Product quantity is required")
    @PositiveOrZero(message = "Product quantity cannot be negative")
    @Schema(
            description = "Available quantity of the product",
            example = "8"
    )
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @Schema(
            description = "Category associated with the product"
    )
    private Category category;

    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}