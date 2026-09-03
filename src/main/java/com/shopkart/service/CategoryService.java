package com.shopkart.service;

import com.shopkart.entity.Category;
import com.shopkart.exception.CategoryInUseException;
import com.shopkart.exception.CategoryNotFoundException;
import com.shopkart.repository.CategoryRepository;
import com.shopkart.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository) {

        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // =========================================================
    // CREATE CATEGORY
    // =========================================================

    public Category createCategory(Category category) {

        return categoryRepository.save(category);
    }

    // =========================================================
    // GET ALL CATEGORIES
    // =========================================================

    public List<Category> getAllCategories() {

        return categoryRepository.findAll();
    }

    // =========================================================
    // GET CATEGORY BY ID
    // =========================================================

    public Category getCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id : " + id
                        ));
    }

    // =========================================================
    // UPDATE CATEGORY
    // =========================================================

    public Category updateCategory(
            Long id,
            Category category) {

        Category existingCategory =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id : "
                                                + id
                                ));

        existingCategory.setName(category.getName());

        return categoryRepository.save(existingCategory);
    }

    // =========================================================
    // DELETE CATEGORY
    // =========================================================

    public String deleteCategory(Long id) {

        Category existingCategory =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id : "
                                                + id
                                ));

        long productCount =
                productRepository.countByCategoryId(id);

        if (productCount > 0) {

            throw new CategoryInUseException(
                    "Cannot delete Category. "
                            + productCount
                            + " Product(s) are using this Category."
            );
        }

        categoryRepository.delete(existingCategory);

        return "Category Deleted Successfully";
    }
}