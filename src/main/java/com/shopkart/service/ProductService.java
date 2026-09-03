package com.shopkart.service;

import com.shopkart.dto.ProductRequest;
import com.shopkart.dto.ProductResponse;
import com.shopkart.dto.ProductUpdateRequest;
import com.shopkart.entity.Category;
import com.shopkart.entity.Product;
import com.shopkart.exception.CategoryNotFoundException;
import com.shopkart.exception.ProductNotFoundException;
import com.shopkart.repository.CategoryRepository;
import com.shopkart.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        if (request.getCategoryId() != null) {

            Category category = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new CategoryNotFoundException(
                                    "Category not found with id : "
                                            + request.getCategoryId()
                            ));

            product.setCategory(category);
        }

        Product savedProduct =
                productRepository.save(product);

        return mapToProductResponse(savedProduct);
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    public Page<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction) {

        Pageable pageable =
                createPageable(page, size, sortBy, direction);

        Page<Product> products =
                productRepository.findAll(pageable);

        return products.map(this::mapToProductResponse);
    }

    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id : "
                                                + id
                                ));

        return mapToProductResponse(product);
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    public ProductResponse updateProduct(
            Long id,
            ProductUpdateRequest request) {

        Product existingProduct =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id : "
                                                + id
                                ));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setQuantity(request.getQuantity());

        if (request.getCategoryId() != null) {

            Category category =
                    categoryRepository
                            .findById(request.getCategoryId())
                            .orElseThrow(() ->
                                    new CategoryNotFoundException(
                                            "Category not found with id : "
                                                    + request.getCategoryId()
                                    ));

            existingProduct.setCategory(category);
        }

        Product updatedProduct =
                productRepository.save(existingProduct);

        return mapToProductResponse(updatedProduct);
    }

    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    public String deleteProduct(Long id) {

        Product existingProduct =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id : "
                                                + id
                                ));

        productRepository.delete(existingProduct);

        return "Product Deleted Successfully";
    }

    // =========================================================
    // SEARCH PRODUCTS BY NAME
    // =========================================================

    public Page<ProductResponse> searchProductsByName(
            String name,
            int page,
            int size,
            String sortBy,
            String direction) {

        Pageable pageable =
                createPageable(page, size, sortBy, direction);

        Page<Product> products =
                productRepository
                        .findByNameContainingIgnoreCase(
                                name,
                                pageable
                        );

        return products.map(this::mapToProductResponse);
    }

    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // =========================================================

    public Page<ProductResponse> getProductsByCategory(
            Long categoryId,
            int page,
            int size,
            String sortBy,
            String direction) {

        categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id : "
                                        + categoryId
                        ));

        Pageable pageable =
                createPageable(page, size, sortBy, direction);

        Page<Product> products =
                productRepository.findByCategoryId(
                        categoryId,
                        pageable
                );

        return products.map(this::mapToProductResponse);
    }

    // =========================================================
    // FILTER PRODUCTS
    // =========================================================

    public Page<ProductResponse> filterProducts(
            Double minPrice,
            Double maxPrice,
            Integer minQuantity,
            Integer maxQuantity,
            int page,
            int size,
            String sortBy,
            String direction) {

        // -----------------------------------------------------
        // Validate price
        // -----------------------------------------------------

        if (minPrice != null && minPrice < 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null && maxPrice < 0) {

            throw new IllegalArgumentException(
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice > maxPrice) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        // -----------------------------------------------------
        // Validate quantity
        // -----------------------------------------------------

        if (minQuantity != null && minQuantity < 0) {

            throw new IllegalArgumentException(
                    "Minimum quantity cannot be negative"
            );
        }

        if (maxQuantity != null && maxQuantity < 0) {

            throw new IllegalArgumentException(
                    "Maximum quantity cannot be negative"
            );
        }

        if (minQuantity != null
                && maxQuantity != null
                && minQuantity > maxQuantity) {

            throw new IllegalArgumentException(
                    "Minimum quantity cannot be greater than maximum quantity"
            );
        }

        // -----------------------------------------------------
        // Create pageable
        // -----------------------------------------------------

        Pageable pageable =
                createPageable(page, size, sortBy, direction);

        Page<Product> products;

        // -----------------------------------------------------
        // Price + Quantity
        // -----------------------------------------------------

        if (minPrice != null
                && maxPrice != null
                && minQuantity != null
                && maxQuantity != null) {

            products =
                    productRepository
                            .findByPriceBetweenAndQuantityBetween(
                                    minPrice,
                                    maxPrice,
                                    minQuantity,
                                    maxQuantity,
                                    pageable
                            );
        }

        // -----------------------------------------------------
        // Price only
        // -----------------------------------------------------

        else if (minPrice != null
                && maxPrice != null) {

            products =
                    productRepository.findByPriceBetween(
                            minPrice,
                            maxPrice,
                            pageable
                    );
        }

        // -----------------------------------------------------
        // Quantity only
        // -----------------------------------------------------

        else if (minQuantity != null
                && maxQuantity != null) {

            products =
                    productRepository.findByQuantityBetween(
                            minQuantity,
                            maxQuantity,
                            pageable
                    );
        }

        // -----------------------------------------------------
        // No filters
        // -----------------------------------------------------

        else {

            products =
                    productRepository.findAll(pageable);
        }

        return products.map(this::mapToProductResponse);
    }

    // =========================================================
    // GET PRODUCTS BY PRICE RANGE
    // =========================================================

    public Page<ProductResponse> getProductsByPriceRange(
            Double minPrice,
            Double maxPrice,
            int page,
            int size,
            String sortBy,
            String direction) {

        // -----------------------------------------------------
        // Validate price
        // -----------------------------------------------------

        if (minPrice == null || maxPrice == null) {

            throw new IllegalArgumentException(
                    "Minimum price and maximum price are required"
            );
        }

        if (minPrice < 0 || maxPrice < 0) {

            throw new IllegalArgumentException(
                    "Price cannot be negative"
            );
        }

        if (minPrice > maxPrice) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        // -----------------------------------------------------
        // Create pageable
        // -----------------------------------------------------

        Pageable pageable =
                createPageable(page, size, sortBy, direction);

        Page<Product> products =
                productRepository.findByPriceBetween(
                        minPrice,
                        maxPrice,
                        pageable
                );

        return products.map(this::mapToProductResponse);
    }

    // =========================================================
    // CREATE PAGEABLE
    // =========================================================

    private Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String direction) {

        validateSortField(sortBy);
        validateSortDirection(direction);

        Sort sort =
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }

    // =========================================================
    // VALIDATE SORT FIELD
    // =========================================================

    private void validateSortField(String sortBy) {

        if (sortBy == null
                || (!sortBy.equals("id")
                && !sortBy.equals("name")
                && !sortBy.equals("price")
                && !sortBy.equals("quantity"))) {

            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }
    }

    // =========================================================
    // VALIDATE SORT DIRECTION
    // =========================================================

    private void validateSortDirection(String direction) {

        if (direction == null
                || (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc"))) {

            throw new IllegalArgumentException(
                    "Invalid sort direction: " + direction
                            + ". Use 'asc' or 'desc'"
            );
        }
    }

    // =========================================================
    // MAP PRODUCT ENTITY TO PRODUCT RESPONSE DTO
    // =========================================================

    private ProductResponse mapToProductResponse(
            Product product) {

        Long categoryId = null;
        String categoryName = null;

        if (product.getCategory() != null) {

            categoryId = product.getCategory().getId();
            categoryName = product.getCategory().getName();
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                categoryId,
                categoryName
        );
    }
}