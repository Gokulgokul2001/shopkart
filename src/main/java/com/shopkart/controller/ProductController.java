package com.shopkart.controller;

import com.shopkart.dto.ProductRequest;
import com.shopkart.dto.ProductResponse;
import com.shopkart.dto.ProductUpdateRequest;
import com.shopkart.response.ApiResponse;
import com.shopkart.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Product APIs",
        description = "APIs for managing products"
)
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @Operation(
            summary = "Create a product",
            description = "Creates a new product and validates its category"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Product created successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid product data"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse createdProduct =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                201,
                                "Product created successfully",
                                createdProduct
                        )
                );
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Operation(
            summary = "Get all products",
            description = "Returns products with pagination and sorting"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid pagination or sorting parameters"
    )
    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(

            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "Page number cannot be negative"
            )
            int page,

            @RequestParam(defaultValue = "5")
            @Min(
                    value = 1,
                    message = "Page size must be at least 1"
            )
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        Page<ProductResponse> products =
                productService.getAllProducts(
                        page,
                        size,
                        sortBy,
                        direction
                );

        return new ApiResponse<>(
                200,
                "Products retrieved successfully",
                products
        );
    }

    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @Operation(
            summary = "Get product by ID",
            description = "Returns a product using its ID"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Product found successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found"
    )
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(
            @PathVariable Long id) {

        ProductResponse product =
                productService.getProductById(id);

        return new ApiResponse<>(
                200,
                "Product retrieved successfully",
                product
        );
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Operation(
            summary = "Update a product",
            description = "Updates an existing product"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Product updated successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid product data"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product or category not found"
    )
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(

            @PathVariable Long id,

            @Valid @RequestBody ProductUpdateRequest request) {

        ProductResponse updatedProduct =
                productService.updateProduct(
                        id,
                        request
                );

        return new ApiResponse<>(
                200,
                "Product updated successfully",
                updatedProduct
        );
    }

    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product using its ID"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Product deleted successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Product not found"
    )
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(
            @PathVariable Long id) {

        String message =
                productService.deleteProduct(id);

        return new ApiResponse<>(
                200,
                message,
                null
        );
    }

    // =========================================================
    // SEARCH PRODUCTS BY NAME
    // =========================================================

    @Operation(
            summary = "Search products by name",
            description = "Searches products using a case-insensitive name"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid pagination or sorting parameters"
    )
    @GetMapping("/search")
    public ApiResponse<Page<ProductResponse>> searchProducts(

            @RequestParam
            String name,

            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "Page number cannot be negative"
            )
            int page,

            @RequestParam(defaultValue = "5")
            @Min(
                    value = 1,
                    message = "Page size must be at least 1"
            )
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        Page<ProductResponse> products =
                productService.searchProductsByName(
                        name,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return new ApiResponse<>(
                200,
                "Products retrieved successfully",
                products
        );
    }

    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // =========================================================

    @Operation(
            summary = "Get products by category",
            description = "Returns products belonging to a specific category"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid pagination or sorting parameters"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @GetMapping("/category/{categoryId}")
    public ApiResponse<Page<ProductResponse>> getProductsByCategory(

            @PathVariable Long categoryId,

            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "Page number cannot be negative"
            )
            int page,

            @RequestParam(defaultValue = "5")
            @Min(
                    value = 1,
                    message = "Page size must be at least 1"
            )
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        Page<ProductResponse> products =
                productService.getProductsByCategory(
                        categoryId,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return new ApiResponse<>(
                200,
                "Products retrieved successfully",
                products
        );
    }

    // =========================================================
    // FILTER PRODUCTS
    // =========================================================

    @Operation(
            summary = "Filter products",
            description = "Filters products by price range and quantity range with pagination and sorting"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Filtered products retrieved successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid filter, pagination, or sorting parameters"
    )
    @GetMapping("/filter")
    public ApiResponse<Page<ProductResponse>> filterProducts(

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice,

            @RequestParam(required = false)
            Integer minQuantity,

            @RequestParam(required = false)
            Integer maxQuantity,

            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "Page number cannot be negative"
            )
            int page,

            @RequestParam(defaultValue = "10")
            @Min(
                    value = 1,
                    message = "Page size must be at least 1"
            )
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        Page<ProductResponse> products =
                productService.filterProducts(
                        minPrice,
                        maxPrice,
                        minQuantity,
                        maxQuantity,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return new ApiResponse<>(
                200,
                "Products filtered successfully",
                products
        );
    }

    // =========================================================
    // GET PRODUCTS BY PRICE RANGE
    // =========================================================

    @Operation(
            summary = "Get products by price range",
            description = "Returns a paginated list of products whose price falls between the given minimum and maximum price"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid price range, pagination, or sorting parameters"
    )
    @GetMapping("/price")
    public ApiResponse<Page<ProductResponse>> getProductsByPriceRange(

            @RequestParam Double minPrice,

            @RequestParam Double maxPrice,

            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "Page number cannot be negative"
            )
            int page,

            @RequestParam(defaultValue = "5")
            @Min(
                    value = 1,
                    message = "Page size must be at least 1"
            )
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        Page<ProductResponse> products =
                productService.getProductsByPriceRange(
                        minPrice,
                        maxPrice,
                        page,
                        size,
                        sortBy,
                        direction
                );

        return new ApiResponse<>(
                200,
                "Products retrieved successfully",
                products
        );
    }
}