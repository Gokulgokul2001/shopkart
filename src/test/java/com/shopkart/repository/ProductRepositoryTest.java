package com.shopkart.repository;

import com.shopkart.entity.Category;
import com.shopkart.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    // =========================================================
    // TEST 1
    // findByNameContainingIgnoreCase()
    // =========================================================

    @Test
    void findByNameContainingIgnoreCase_success() {

        Product product = new Product();
        product.setName("RepositoryTestLaptop");
        product.setDescription("Gaming Laptop");
        product.setPrice(75000.0);
        product.setQuantity(10);

        productRepository.save(product);

        Page<Product> result =
                productRepository.findByNameContainingIgnoreCase(
                        "repositorytestlaptop",
                        PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Product found = result.getContent().get(0);

        assertEquals(
                "RepositoryTestLaptop",
                found.getName());

        assertEquals(
                75000.0,
                found.getPrice());

        assertEquals(
                10,
                found.getQuantity());
    }


    // =========================================================
    // TEST 2
    // findByCategoryId()
    // =========================================================

    @Test
    void findByCategoryId_success() {

        Category category = new Category();
        category.setName("RepositoryTestCategory2");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("RepositoryTestProduct2");
        product.setDescription("Category Test Product");
        product.setPrice(50000.0);
        product.setQuantity(5);
        product.setCategory(savedCategory);

        productRepository.save(product);

        Page<Product> result =
                productRepository.findByCategoryId(
                        savedCategory.getId(),
                        PageRequest.of(0, 5));

        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 1);

        boolean productFound =
                result.getContent()
                        .stream()
                        .anyMatch(p ->
                                "RepositoryTestProduct2"
                                        .equals(p.getName()));

        assertTrue(productFound);
    }


    // =========================================================
    // TEST 3
    // findByCategoryIdAndNameContainingIgnoreCase()
    // =========================================================

    @Test
    void findByCategoryIdAndNameContainingIgnoreCase_success() {

        Category category = new Category();
        category.setName("RepositoryTestCategory3");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("RepositoryTestPhone");
        product.setDescription("Test Phone");
        product.setPrice(30000.0);
        product.setQuantity(8);
        product.setCategory(savedCategory);

        productRepository.save(product);

        Page<Product> result =
                productRepository
                        .findByCategoryIdAndNameContainingIgnoreCase(
                                savedCategory.getId(),
                                "repositorytestphone",
                                PageRequest.of(0, 5));

        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 1);

        boolean productFound =
                result.getContent()
                        .stream()
                        .anyMatch(p ->
                                "RepositoryTestPhone"
                                        .equals(p.getName()));

        assertTrue(productFound);
    }


    // =========================================================
    // TEST 4
    // findByPriceBetween()
    // =========================================================

    @Test
    void findByPriceBetween_success() {

        Product product = new Product();
        product.setName("RepositoryTestPriceProduct");
        product.setDescription("Price Range Test");
        product.setPrice(75000.0);
        product.setQuantity(10);

        productRepository.save(product);

        Page<Product> result =
                productRepository.findByPriceBetween(
                        70000.0,
                        80000.0,
                        PageRequest.of(0, 100));

        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 1);

        boolean productFound =
                result.getContent()
                        .stream()
                        .anyMatch(p ->
                                "RepositoryTestPriceProduct"
                                        .equals(p.getName()));

        assertTrue(productFound);
    }


    // =========================================================
    // TEST 5
    // findByQuantityBetween()
    // =========================================================

    @Test
    void findByQuantityBetween_success() {

        Product product = new Product();
        product.setName("RepositoryTestQuantityProduct");
        product.setDescription("Quantity Range Test");
        product.setPrice(45000.0);
        product.setQuantity(7);

        productRepository.save(product);

        Page<Product> result =
                productRepository.findByQuantityBetween(
                        5,
                        10,
                        PageRequest.of(
                                0,
                                100,
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "id"
                                )));

        assertNotNull(result);
        assertTrue(result.getTotalElements() >= 1);

        boolean productFound =
                result.getContent()
                        .stream()
                        .anyMatch(p ->
                                "RepositoryTestQuantityProduct"
                                        .equals(p.getName()));

        assertTrue(productFound);
    }


    // =========================================================
    // TEST 6
    // findByPriceBetweenAndQuantityBetween()
    // =========================================================

    @Test
    void findByPriceBetweenAndQuantityBetween_success() {

        Product product = new Product();
        product.setName("RepositoryTestCombinedProduct");
        product.setDescription("Combined Filter Test");
        product.setPrice(85000.0);
        product.setQuantity(8);

        productRepository.save(product);

        Page<Product> result =
                productRepository
                        .findByPriceBetweenAndQuantityBetween(
                                80000.0,
                                90000.0,
                                5,
                                10,
                                PageRequest.of(0, 100));

        assertNotNull(result);

        boolean productFound =
                result.getContent()
                        .stream()
                        .anyMatch(p ->
                                "RepositoryTestCombinedProduct"
                                        .equals(p.getName()));

        assertTrue(productFound);
    }


    // =========================================================
    // TEST 7
    // countByCategoryId()
    // =========================================================

    @Test
    void countByCategoryId_success() {

        Category category = new Category();
        category.setName("RepositoryTestCategory7");

        Category savedCategory =
                categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("RepositoryTestCountProduct1");
        product1.setDescription("Count Test Product 1");
        product1.setPrice(20000.0);
        product1.setQuantity(5);
        product1.setCategory(savedCategory);

        Product product2 = new Product();
        product2.setName("RepositoryTestCountProduct2");
        product2.setDescription("Count Test Product 2");
        product2.setPrice(30000.0);
        product2.setQuantity(10);
        product2.setCategory(savedCategory);

        productRepository.save(product1);
        productRepository.save(product2);

        long count =
                productRepository.countByCategoryId(
                        savedCategory.getId());

        assertEquals(2, count);
    }


    // =========================================================
    // TEST 8
    // Pagination
    // =========================================================

    @Test
    void findByNameContainingIgnoreCase_pagination() {

        Product product1 = new Product();
        product1.setName("RepositoryPageProduct1");
        product1.setDescription("Pagination Product 1");
        product1.setPrice(10000.0);
        product1.setQuantity(5);

        Product product2 = new Product();
        product2.setName("RepositoryPageProduct2");
        product2.setDescription("Pagination Product 2");
        product2.setPrice(20000.0);
        product2.setQuantity(5);

        Product product3 = new Product();
        product3.setName("RepositoryPageProduct3");
        product3.setDescription("Pagination Product 3");
        product3.setPrice(30000.0);
        product3.setQuantity(5);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Page<Product> result =
                productRepository.findByNameContainingIgnoreCase(
                        "repositorypageproduct",
                        PageRequest.of(0, 2));

        assertNotNull(result);

        assertEquals(2, result.getContent().size());

        assertEquals(3, result.getTotalElements());

        assertEquals(2, result.getTotalPages());

        assertEquals(0, result.getNumber());
    }


    // =========================================================
    // TEST 9
    // Sorting
    // =========================================================

    @Test
    void findByNameContainingIgnoreCase_sorting() {

        Product product1 = new Product();
        product1.setName("RepositorySortProductA");
        product1.setDescription("Sorting Product A");
        product1.setPrice(30000.0);
        product1.setQuantity(5);

        Product product2 = new Product();
        product2.setName("RepositorySortProductB");
        product2.setDescription("Sorting Product B");
        product2.setPrice(10000.0);
        product2.setQuantity(5);

        Product product3 = new Product();
        product3.setName("RepositorySortProductC");
        product3.setDescription("Sorting Product C");
        product3.setPrice(20000.0);
        product3.setQuantity(5);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Page<Product> result =
                productRepository.findByNameContainingIgnoreCase(
                        "repositorysortproduct",
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "price")));

        assertNotNull(result);

        assertEquals(3, result.getTotalElements());

        assertEquals(
                10000.0,
                result.getContent().get(0).getPrice());

        assertEquals(
                20000.0,
                result.getContent().get(1).getPrice());

        assertEquals(
                30000.0,
                result.getContent().get(2).getPrice());
    }


    // =========================================================
    // TEST 10
    // Empty Result
    // =========================================================

    @Test
    void findByNameContainingIgnoreCase_noResults() {

        Page<Product> result =
                productRepository.findByNameContainingIgnoreCase(
                        "NoSuchRepositoryProduct999999",
                        PageRequest.of(0, 5));

        assertNotNull(result);

        assertTrue(result.isEmpty());

        assertEquals(0, result.getTotalElements());

        assertEquals(0, result.getTotalPages());
    }
}