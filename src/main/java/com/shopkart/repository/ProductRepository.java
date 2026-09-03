package com.shopkart.repository;

import com.shopkart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Count products by category
    long countByCategoryId(Long categoryId);

    // Search products by name
    Page<Product> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable);

    // Get products by category
    Page<Product> findByCategoryId(
            Long categoryId,
            Pageable pageable);

    // Search products by category and name
    Page<Product> findByCategoryIdAndNameContainingIgnoreCase(
            Long categoryId,
            String name,
            Pageable pageable);

    // Filter products by price range
    Page<Product> findByPriceBetween(
            Double minPrice,
            Double maxPrice,
            Pageable pageable);

    // Filter products by quantity range
    Page<Product> findByQuantityBetween(
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable);

    // Filter products by price and quantity range
    Page<Product> findByPriceBetweenAndQuantityBetween(
            Double minPrice,
            Double maxPrice,
            Integer minQuantity,
            Integer maxQuantity,
            Pageable pageable);
}