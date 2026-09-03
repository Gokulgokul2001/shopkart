package com.shopkart.controller;

import com.shopkart.entity.Category;
import com.shopkart.response.ApiResponse;
import com.shopkart.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(
        name = "Category APIs",
        description = "APIs for managing product categories"
)
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    @Operation(
            summary = "Create a new category",
            description = "Creates a new product category."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Category created successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid category data"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @Valid @RequestBody Category category) {

        Category createdCategory =
                categoryService.createCategory(category);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                201,
                                "Category created successfully",
                                createdCategory
                        )
                );
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    @Operation(
            summary = "Get all categories",
            description = "Returns a list of all product categories."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {

        List<Category> categories =
                categoryService.getAllCategories();

        return ResponseEntity
                .ok(
                        new ApiResponse<>(
                                200,
                                "Categories retrieved successfully",
                                categories
                        )
                );
    }

    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================

    @Operation(
            summary = "Get category by ID",
            description = "Returns a product category using its unique ID."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Category found successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(
            @PathVariable Long id) {

        Category category =
                categoryService.getCategoryById(id);

        return ResponseEntity
                .ok(
                        new ApiResponse<>(
                                200,
                                "Category retrieved successfully",
                                category
                        )
                );
    }

    // =========================================================
    // UPDATE CATEGORY
    // =========================================================

    @Operation(
            summary = "Update category",
            description = "Updates an existing product category using its ID."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Category updated successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid category data"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {

        Category updatedCategory =
                categoryService.updateCategory(
                        id,
                        category
                );

        return ResponseEntity
                .ok(
                        new ApiResponse<>(
                                200,
                                "Category updated successfully",
                                updatedCategory
                        )
                );
    }

    // =========================================================
    // DELETE CATEGORY
    // =========================================================

    @Operation(
            summary = "Delete category",
            description = "Deletes a category if no products are currently using it."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Category deleted successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Category not found"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Category cannot be deleted because products are using it"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @PathVariable Long id) {

        String message =
                categoryService.deleteCategory(id);

        return ResponseEntity
                .ok(
                        new ApiResponse<>(
                                200,
                                "Category deleted successfully",
                                message
                        )
                );
    }
}