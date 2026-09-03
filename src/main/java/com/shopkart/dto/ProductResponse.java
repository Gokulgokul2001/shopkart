package com.shopkart.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Long categoryId;
    private String categoryName;

    public ProductResponse() {
    }

    public ProductResponse(
            Long id,
            String name,
            String description,
            Double price,
            Integer quantity,
            Long categoryId,
            String categoryName) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}