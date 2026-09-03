package com.shopkart.service;

import com.shopkart.dto.ProductRequest;
import com.shopkart.dto.ProductResponse;
import com.shopkart.entity.Product;
import com.shopkart.exception.CategoryNotFoundException;
import com.shopkart.exception.ProductNotFoundException;
import com.shopkart.repository.CategoryRepository;
import com.shopkart.repository.ProductRepository;
import com.shopkart.dto.ProductUpdateRequest;
import com.shopkart.entity.Category;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_shouldReturnProductResponse() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("Dell Inspiron Laptop");
        request.setPrice(65000.0);
        request.setQuantity(5);

        Product savedProduct = new Product();
        savedProduct.setId(10L);
        savedProduct.setName("Laptop");
        savedProduct.setDescription("Dell Inspiron Laptop");
        savedProduct.setPrice(65000.0);
        savedProduct.setQuantity(5);

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        // Act
        ProductResponse response =
                productService.createProduct(request);

        // Assert
        assertNotNull(response);

        assertEquals(10L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals("Dell Inspiron Laptop", response.getDescription());
        assertEquals(65000.0, response.getPrice());
        assertEquals(5, response.getQuantity());

        // Verify repository save was called once
        verify(productRepository, times(1))
                .save(any(Product.class));
    }
    @Test
    void createProduct_shouldThrowException_whenCategoryNotFound() {

        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("Dell Inspiron Laptop");
        request.setPrice(65000.0);
        request.setQuantity(5);
        request.setCategoryId(99L);

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        CategoryNotFoundException exception =
                assertThrows(
                        CategoryNotFoundException.class,
                        () -> productService.createProduct(request)
                );

        assertEquals(
                "Category not found with id : 99",
                exception.getMessage()
        );

        // Verify product should NOT be saved
        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnProductResponse() {

        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        // Act
        ProductResponse response =
                productService.getProductById(10L);

        // Assert
        assertNotNull(response);

        assertEquals(10L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals("Dell Inspiron Laptop", response.getDescription());
        assertEquals(65000.0, response.getPrice());
        assertEquals(5, response.getQuantity());

        // Verify repository was called once
        verify(productRepository, times(1))
                .findById(10L);
    }
    @Test
    void getProductById_shouldThrowException_whenProductNotFound() {

        // Arrange
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService.getProductById(99L)
                );

        assertEquals(
                "Product not found with id : 99",
                exception.getMessage()
        );

        // Verify repository was called once
        verify(productRepository, times(1))
                .findById(99L);
    }
    @Test
    void updateProduct_shouldReturnUpdatedProductResponse() {

        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(10L);
        existingProduct.setName("Laptop");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(60000.0);
        existingProduct.setQuantity(3);

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated Dell Laptop");
        request.setPrice(65000.0);
        request.setQuantity(5);

        Product updatedProduct = new Product();
        updatedProduct.setId(10L);
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setDescription("Updated Dell Laptop");
        updatedProduct.setPrice(65000.0);
        updatedProduct.setQuantity(5);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class)))
                .thenReturn(updatedProduct);

        // Act
        ProductResponse response =
                productService.updateProduct(10L, request);

        // Assert
        assertNotNull(response);

        assertEquals(10L, response.getId());
        assertEquals("Updated Laptop", response.getName());
        assertEquals("Updated Dell Laptop", response.getDescription());
        assertEquals(65000.0, response.getPrice());
        assertEquals(5, response.getQuantity());

        // Verify repository calls
        verify(productRepository, times(1))
                .findById(10L);

        verify(productRepository, times(1))
                .save(any(Product.class));
    }
    @Test
    void updateProduct_shouldThrowException_whenProductNotFound() {

        // Arrange
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated Dell Laptop");
        request.setPrice(65000.0);
        request.setQuantity(5);

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService.updateProduct(99L, request)
                );

        assertEquals(
                "Product not found with id : 99",
                exception.getMessage()
        );

        // Product should not be saved
        verify(productRepository, never())
                .save(any(Product.class));
    }
    @Test
    void updateProduct_withoutCategory_success() {

        Long productId = 1L;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Product");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(50000.0);
        existingProduct.setQuantity(5);

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(60000.0);
        request.setQuantity(10);
        request.setCategoryId(null);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        ProductResponse response =
                productService.updateProduct(productId, request);

        assertNotNull(response);

        assertEquals(
                "Updated Product",
                response.getName());

        assertEquals(
                "Updated Description",
                response.getDescription());

        assertEquals(
                60000.0,
                response.getPrice());

        assertEquals(
                10,
                response.getQuantity());

        verify(productRepository)
                .findById(productId);

        verify(productRepository)
                .save(existingProduct);

        verify(categoryRepository, never())
                .findById(anyLong());
    }
    @Test
    void deleteProduct_shouldDeleteProductSuccessfully() {

        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        // Act
        String response = productService.deleteProduct(10L);

        // Assert
        assertEquals(
                "Product Deleted Successfully",
                response
        );

        // Verify product was deleted
        verify(productRepository, times(1))
                .delete(product);
    }
    @Test
    void deleteProduct_shouldThrowException_whenProductNotFound() {

        // Arrange
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> productService.deleteProduct(99L)
                );

        assertEquals(
                "Product not found with id : 99",
                exception.getMessage()
        );

        // Verify product was NOT deleted
        verify(productRepository, never())
                .delete(any(Product.class));
    }
    @Test
    void searchProductsByName_shouldReturnProductResponses() {

        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);

        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                );

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(productRepository.findByNameContainingIgnoreCase(
                "lap",
                pageable))
                .thenReturn(productPage);

        // Act
        Page<ProductResponse> response =
                productService.searchProductsByName(
                        "lap",
                        0,
                        5,
                        "id",
                        "asc"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        ProductResponse productResponse =
                response.getContent().get(0);

        assertEquals(
                10L,
                productResponse.getId()
        );

        assertEquals(
                "Laptop",
                productResponse.getName()
        );

        assertEquals(
                "Dell Inspiron Laptop",
                productResponse.getDescription()
        );

        assertEquals(
                65000.0,
                productResponse.getPrice()
        );

        assertEquals(
                5,
                productResponse.getQuantity()
        );

        // Verify repository was called once
        verify(productRepository, times(1))
                .findByNameContainingIgnoreCase(
                        "lap",
                        pageable
                );
    }
    @Test
    void searchProductsByName_shouldReturnEmptyPage_whenNoProductFound() {

        // Arrange
        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                );

        Page<Product> emptyPage =
                new PageImpl<>(List.of());

        when(productRepository.findByNameContainingIgnoreCase(
                "mobile",
                pageable))
                .thenReturn(emptyPage);

        // Act
        Page<ProductResponse> response =
                productService.searchProductsByName(
                        "mobile",
                        0,
                        5,
                        "id",
                        "asc"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                0,
                response.getTotalElements()
        );

        assertTrue(
                response.getContent().isEmpty()
        );

        // Verify repository was called once
        verify(productRepository, times(1))
                .findByNameContainingIgnoreCase(
                        "mobile",
                        pageable
                );
    }
    @Test
    void getProductsByCategory_shouldReturnProductResponses() {

        // Arrange
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);
        product.setCategory(category);

        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                );

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.findByCategoryId(
                categoryId,
                pageable))
                .thenReturn(productPage);

        // Act
        Page<ProductResponse> response =
                productService.getProductsByCategory(
                        categoryId,
                        0,
                        5,
                        "id",
                        "asc"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        ProductResponse productResponse =
                response.getContent().get(0);

        assertEquals(
                10L,
                productResponse.getId()
        );

        assertEquals(
                "Laptop",
                productResponse.getName()
        );

        assertEquals(
                "Dell Inspiron Laptop",
                productResponse.getDescription()
        );

        assertEquals(
                65000.0,
                productResponse.getPrice()
        );

        assertEquals(
                5,
                productResponse.getQuantity()
        );

        assertEquals(
                1L,
                productResponse.getCategoryId()
        );

        assertEquals(
                "Electronics",
                productResponse.getCategoryName()
        );

        // Verify category was checked
        verify(categoryRepository, times(1))
                .findById(categoryId);

        // Verify products were fetched
        verify(productRepository, times(1))
                .findByCategoryId(
                        categoryId,
                        pageable
                );
    }
    @Test
    void getProductsByCategory_shouldThrowException_whenCategoryNotFound() {

        // Arrange
        Long categoryId = 99L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        CategoryNotFoundException exception =
                assertThrows(
                        CategoryNotFoundException.class,
                        () -> productService.getProductsByCategory(
                                categoryId,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Category not found with id : 99",
                exception.getMessage()
        );

        // Verify category was checked
        verify(categoryRepository, times(1))
                .findById(categoryId);

        // Products should NOT be fetched
        verify(productRepository, never())
                .findByCategoryId(
                        anyLong(),
                        any(Pageable.class)
                );
    }
    @Test
    void filterProducts_shouldReturnFilteredProducts() {

        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);

        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                );

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(productRepository.findByPriceBetweenAndQuantityBetween(
                50000.0,
                150000.0,
                5,
                10,
                pageable
        )).thenReturn(productPage);

        // Act
        Page<ProductResponse> response =
                productService.filterProducts(
                        50000.0,
                        150000.0,
                        5,
                        10,
                        0,
                        5,
                        "id",
                        "asc"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        ProductResponse productResponse =
                response.getContent().get(0);

        assertEquals(
                10L,
                productResponse.getId()
        );

        assertEquals(
                "Laptop",
                productResponse.getName()
        );

        assertEquals(
                65000.0,
                productResponse.getPrice()
        );

        assertEquals(
                5,
                productResponse.getQuantity()
        );

        // Verify correct repository method was called
        verify(productRepository, times(1))
                .findByPriceBetweenAndQuantityBetween(
                        50000.0,
                        150000.0,
                        5,
                        10,
                        pageable
                );
    }
    @Test
    void filterProducts_shouldFilterByPriceOnly() {

        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);

        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                );

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(productRepository.findByPriceBetween(
                50000.0,
                100000.0,
                pageable
        )).thenReturn(productPage);

        // Act
        Page<ProductResponse> response =
                productService.filterProducts(
                        50000.0,
                        100000.0,
                        null,
                        null,
                        0,
                        5,
                        "id",
                        "asc"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        ProductResponse productResponse =
                response.getContent().get(0);

        assertEquals(
                10L,
                productResponse.getId()
        );

        assertEquals(
                "Laptop",
                productResponse.getName()
        );

        assertEquals(
                65000.0,
                productResponse.getPrice()
        );

        assertEquals(
                5,
                productResponse.getQuantity()
        );

        // Verify price-only repository method
        verify(productRepository, times(1))
                .findByPriceBetween(
                        50000.0,
                        100000.0,
                        pageable
                );
    }
    @Test
    void filterProducts_shouldFilterByQuantityOnly() {

        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);

        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                );

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(productRepository.findByQuantityBetween(
                5,
                10,
                pageable
        )).thenReturn(productPage);

        // Act
        Page<ProductResponse> response =
                productService.filterProducts(
                        null,
                        null,
                        5,
                        10,
                        0,
                        5,
                        "id",
                        "asc"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        ProductResponse productResponse =
                response.getContent().get(0);

        assertEquals(
                10L,
                productResponse.getId()
        );

        assertEquals(
                "Laptop",
                productResponse.getName()
        );

        assertEquals(
                65000.0,
                productResponse.getPrice()
        );

        assertEquals(
                5,
                productResponse.getQuantity()
        );

        // Verify quantity-only repository method
        verify(productRepository, times(1))
                .findByQuantityBetween(
                        5,
                        10,
                        pageable
                );
    }
    @Test
    void filterProducts_shouldReturnAllProductsWhenNoFiltersProvided() {

        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Dell Inspiron Laptop");
        product.setPrice(65000.0);
        product.setQuantity(5);

        Pageable pageable =
                PageRequest.of(
                        0,
                        5,
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                );

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        // Act
        Page<ProductResponse> response =
                productService.filterProducts(
                        null,
                        null,
                        null,
                        null,
                        0,
                        5,
                        "id",
                        "asc"
                );

        // Assert
        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        ProductResponse productResponse =
                response.getContent().get(0);

        assertEquals(
                10L,
                productResponse.getId()
        );

        assertEquals(
                "Laptop",
                productResponse.getName()
        );

        assertEquals(
                65000.0,
                productResponse.getPrice()
        );

        assertEquals(
                5,
                productResponse.getQuantity()
        );

        // Verify findAll is called
        verify(productRepository, times(1))
                .findAll(pageable);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenMinPriceGreaterThanMaxPrice() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                150000.0,
                                50000.0,
                                null,
                                null,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Minimum price cannot be greater than maximum price",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenMinQuantityGreaterThanMaxQuantity() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                10,
                                5,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Minimum quantity cannot be greater than maximum quantity",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenMinPriceIsNegative() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                -100.0,
                                50000.0,
                                null,
                                null,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Minimum price cannot be negative",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenMaxPriceIsNegative() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                50000.0,
                                -100.0,
                                null,
                                null,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Maximum price cannot be negative",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenMinQuantityIsNegative() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                -5,
                                10,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Minimum quantity cannot be negative",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenMaxQuantityIsNegative() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                5,
                                -10,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Maximum quantity cannot be negative",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenPageIsNegative() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                null,
                                null,
                                -1,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Page index must not be less than zero",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenSizeIsNegative() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                null,
                                null,
                                0,
                                -1,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Page size must not be less than one",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenSortFieldIsInvalid() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                null,
                                null,
                                0,
                                5,
                                "invalidField",
                                "asc"
                        )
                );

        assertEquals(
                "Invalid sort field: invalidField",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenSortDirectionIsInvalid() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                null,
                                null,
                                0,
                                5,
                                "id",
                                "invalid"
                        )
                );

        assertEquals(
                "Invalid sort direction: invalid. Use 'asc' or 'desc'",
                exception.getMessage()
        );

        // Repository should not be called
        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldWorkWithDescendingSort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product2, product1));

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.filterProducts(
                        null,
                        null,
                        null,
                        null,
                        0,
                        5,
                        "price",
                        "desc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findAll(
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.DESC, "price")
                )
        );
    }
    @Test
    void filterProducts_shouldWorkWithAscendingSort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2));

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.filterProducts(
                        null,
                        null,
                        null,
                        null,
                        0,
                        5,
                        "price",
                        "asc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findAll(
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.ASC, "price")
                )
        );
    }
    @Test
    void filterProducts_shouldWorkWithNameSort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2));

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.filterProducts(
                        null,
                        null,
                        null,
                        null,
                        0,
                        5,
                        "name",
                        "asc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findAll(
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.ASC, "name")
                )
        );
    }
    @Test
    void getProductsByPriceRange_shouldReturnProductsSuccessfully() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2));

        when(productRepository.findByPriceBetween(
                eq(50000.0),
                eq(80000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        0,
                        5,
                        "price",
                        "asc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findByPriceBetween(
                50000.0,
                80000.0,
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.ASC, "price")
                )
        );
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenMinPriceIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                null,
                                80000.0,
                                0,
                                5,
                                "price",
                                "asc"
                        )
                );

        assertEquals(
                "Minimum price and maximum price are required",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenMaxPriceIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                50000.0,
                                null,
                                0,
                                5,
                                "price",
                                "asc"
                        )
                );

        assertEquals(
                "Minimum price and maximum price are required",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenMinPriceIsGreaterThanMaxPrice() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                100000.0,
                                50000.0,
                                0,
                                5,
                                "price",
                                "asc"
                        )
                );

        assertEquals(
                "Minimum price cannot be greater than maximum price",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenMinPriceIsNegative() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                -100.0,
                                50000.0,
                                0,
                                5,
                                "price",
                                "asc"
                        )
                );

        assertEquals(
                "Price cannot be negative",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenMaxPriceIsNegative() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                50000.0,
                                -100.0,
                                0,
                                5,
                                "price",
                                "asc"
                        )
                );

        assertEquals(
                "Price cannot be negative",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenPageIsNegative() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                50000.0,
                                80000.0,
                                -1,
                                5,
                                "price",
                                "asc"
                        )
                );

        assertEquals(
                "Page index must not be less than zero",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenSizeIsNegative() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                50000.0,
                                80000.0,
                                0,
                                -1,
                                "price",
                                "asc"
                        )
                );

        assertEquals(
                "Page size must not be less than one",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenSortFieldIsInvalid() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                50000.0,
                                80000.0,
                                0,
                                5,
                                "invalidField",
                                "asc"
                        )
                );

        assertEquals(
                "Invalid sort field: invalidField",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldThrowExceptionWhenSortDirectionIsInvalid() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.getProductsByPriceRange(
                                50000.0,
                                80000.0,
                                0,
                                5,
                                "price",
                                "invalid"
                        )
                );

        assertEquals(
                "Invalid sort direction: invalid. Use 'asc' or 'desc'",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldWorkWithDescendingSort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product2, product1));

        when(productRepository.findByPriceBetween(
                eq(50000.0),
                eq(80000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        0,
                        5,
                        "price",
                        "desc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findByPriceBetween(
                50000.0,
                80000.0,
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.DESC, "price")
                )
        );
    }
    @Test
    void getProductsByPriceRange_shouldWorkWithAscendingSort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2));

        when(productRepository.findByPriceBetween(
                eq(50000.0),
                eq(80000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        0,
                        5,
                        "price",
                        "asc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findByPriceBetween(
                50000.0,
                80000.0,
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.ASC, "price")
                )
        );
    }
    @Test
    void getProductsByPriceRange_shouldWorkWithNameSort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2));

        when(productRepository.findByPriceBetween(
                eq(50000.0),
                eq(80000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        0,
                        5,
                        "name",
                        "asc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findByPriceBetween(
                50000.0,
                80000.0,
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.ASC, "name")
                )
        );
    }
    @Test
    void getProductsByPriceRange_shouldWorkWithQuantitySort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product2, product1));

        when(productRepository.findByPriceBetween(
                eq(50000.0),
                eq(80000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        0,
                        5,
                        "quantity",
                        "desc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findByPriceBetween(
                50000.0,
                80000.0,
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.DESC, "quantity")
                )
        );
    }
    @Test
    void getProductsByPriceRange_shouldWorkWithIdSort() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(50000.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(80000.0);
        product2.setQuantity(5);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2));

        when(productRepository.findByPriceBetween(
                eq(50000.0),
                eq(80000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        verify(productRepository).findByPriceBetween(
                50000.0,
                80000.0,
                PageRequest.of(
                        0,
                        5,
                        Sort.by(Sort.Direction.ASC, "id")
                )
        );
    }
    @Test
    void getProductsByPriceRange_shouldNotCallRepositoryWhenRangeIsInvalid() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        100000.0,
                        50000.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldNotCallRepositoryWhenMinPriceIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        null,
                        50000.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldNotCallRepositoryWhenMaxPriceIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        50000.0,
                        null,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldNotCallRepositoryWhenMinPriceIsNegative() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        -100.0,
                        50000.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldNotCallRepositoryWhenMaxPriceIsNegative() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        50000.0,
                        -100.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldNotCallRepositoryWhenPageIsNegative() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        -1,
                        5,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_shouldNotCallRepositoryWhenSizeIsNegative() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        50000.0,
                        80000.0,
                        0,
                        -1,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_invalidSortField_repositoryNotCalled() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        0,
                        5,
                        "invalidField",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_invalidSortDirection_repositoryNotCalled() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        0,
                        5,
                        "price",
                        "invalid"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_validRequest_repositoryCalledOnce() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        productService.getProductsByPriceRange(
                100.0,
                1000.0,
                0,
                5,
                "price",
                "asc"
        );

        verify(productRepository, times(1)).findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        );
    }
    @Test
    void getProductsByPriceRange_returnsProductResponse() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming Laptop");
        product.setPrice(999.0);
        product.setQuantity(5);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        0,
                        5,
                        "price",
                        "asc"
                );

        assertEquals(1, result.getTotalElements());

        ProductResponse response = result.getContent().get(0);

        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals("Gaming Laptop", response.getDescription());
        assertEquals(999.0, response.getPrice());
        assertEquals(5, response.getQuantity());
    }
    @Test
    void getProductsByPriceRange_returnsCategoryDetails() {

        Category category = new Category();
        category.setId(10L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming Laptop");
        product.setPrice(999.0);
        product.setQuantity(5);
        product.setCategory(category);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        0,
                        5,
                        "price",
                        "asc"
                );

        ProductResponse response = result.getContent().get(0);

        assertEquals(10L, response.getCategoryId());
        assertEquals("Electronics", response.getCategoryName());
    }
    @Test
    void getProductsByPriceRange_productWithoutCategory_returnsNullCategoryDetails() {

        Product product = new Product();
        product.setId(2L);
        product.setName("Mouse");
        product.setDescription("Wireless Mouse");
        product.setPrice(500.0);
        product.setQuantity(10);
        product.setCategory(null);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        0,
                        5,
                        "price",
                        "asc"
                );

        ProductResponse response = result.getContent().get(0);

        assertNull(response.getCategoryId());
        assertNull(response.getCategoryName());
    }
    @Test
    void getProductsByPriceRange_preservesPaginationMetadata() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(900.0);
        product1.setQuantity(5);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Monitor");
        product2.setPrice(700.0);
        product2.setQuantity(3);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product1, product2),
                        PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "price")),
                        6
                );

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        1,
                        2,
                        "price",
                        "asc"
                );

        assertEquals(1, result.getNumber());
        assertEquals(2, result.getSize());
        assertEquals(6, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(2, result.getContent().size());
    }
    @Test
    void getProductsByPriceRange_passesCorrectPageable() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        productService.getProductsByPriceRange(
                100.0,
                1000.0,
                2,
                10,
                "price",
                "desc"
        );

        verify(productRepository).findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                argThat(pageable ->
                        pageable.getPageNumber() == 2
                                && pageable.getPageSize() == 10
                                && pageable.getSort().getOrderFor("price") != null
                                && pageable.getSort().getOrderFor("price")
                                .getDirection() == Sort.Direction.DESC
                )
        );
    }
    @Test
    void getProductsByPriceRange_idSort() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        productService.getProductsByPriceRange(
                100.0,
                1000.0,
                0,
                5,
                "id",
                "asc"
        );

        verify(productRepository).findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                argThat(pageable ->
                        pageable.getSort().getOrderFor("id") != null
                                && pageable.getSort()
                                .getOrderFor("id")
                                .getDirection() == Sort.Direction.ASC
                )
        );
    }
    @Test
    void getProductsByPriceRange_quantitySort() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        productService.getProductsByPriceRange(
                100.0,
                1000.0,
                0,
                5,
                "quantity",
                "desc"
        );

        verify(productRepository).findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                argThat(pageable ->
                        pageable.getSort().getOrderFor("quantity") != null
                                && pageable.getSort()
                                .getOrderFor("quantity")
                                .getDirection() == Sort.Direction.DESC
                )
        );
    }
    @Test
    void getProductsByPriceRange_nameSort() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        productService.getProductsByPriceRange(
                100.0,
                1000.0,
                0,
                5,
                "name",
                "asc"
        );

        verify(productRepository).findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                argThat(pageable ->
                        pageable.getSort().getOrderFor("name") != null
                                && pageable.getSort()
                                .getOrderFor("name")
                                .getDirection() == Sort.Direction.ASC
                )
        );
    }
    @Test
    void getProductsByPriceRange_invalidRange_repositoryNotCalled() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductsByPriceRange(
                        1000.0,
                        100.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void getProductsByPriceRange_zeroMinPrice_isAccepted() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(0.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        assertDoesNotThrow(() ->
                productService.getProductsByPriceRange(
                        0.0,
                        1000.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verify(productRepository).findByPriceBetween(
                eq(0.0),
                eq(1000.0),
                any(Pageable.class)
        );
    }
    @Test
    void getProductsByPriceRange_zeroMaxPrice_isAccepted() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(0.0),
                eq(0.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        assertDoesNotThrow(() ->
                productService.getProductsByPriceRange(
                        0.0,
                        0.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verify(productRepository).findByPriceBetween(
                eq(0.0),
                eq(0.0),
                any(Pageable.class)
        );
    }
    @Test
    void getProductsByPriceRange_equalMinAndMaxPrice_isAccepted() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(500.0),
                eq(500.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        assertDoesNotThrow(() ->
                productService.getProductsByPriceRange(
                        500.0,
                        500.0,
                        0,
                        5,
                        "price",
                        "asc"
                )
        );

        verify(productRepository).findByPriceBetween(
                eq(500.0),
                eq(500.0),
                any(Pageable.class)
        );
    }
    @Test
    void getProductsByPriceRange_noProducts_returnsEmptyPage() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        0,
                        5,
                        "price",
                        "asc"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }
    @Test
    void getProductsByPriceRange_singleProduct_returnsCorrectResponse() {

        Product product = new Product();
        product.setId(10L);
        product.setName("Keyboard");
        product.setDescription("Mechanical Keyboard");
        product.setPrice(750.0);
        product.setQuantity(4);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findByPriceBetween(
                eq(500.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        500.0,
                        1000.0,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        ProductResponse response = result.getContent().get(0);

        assertEquals(10L, response.getId());
        assertEquals("Keyboard", response.getName());
        assertEquals(750.0, response.getPrice());
        assertEquals(4, response.getQuantity());
    }
    @Test
    void getProductsByPriceRange_multipleProducts_returnsAllResponses() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(900.0);
        product1.setQuantity(5);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Monitor");
        product2.setPrice(700.0);
        product2.setQuantity(3);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Keyboard");
        product3.setPrice(500.0);
        product3.setQuantity(10);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2, product3));

        when(productRepository.findByPriceBetween(
                eq(500.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        500.0,
                        1000.0,
                        0,
                        5,
                        "price",
                        "asc"
                );

        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());

        assertEquals("Laptop", result.getContent().get(0).getName());
        assertEquals("Monitor", result.getContent().get(1).getName());
        assertEquals("Keyboard", result.getContent().get(2).getName());

        assertEquals(900.0, result.getContent().get(0).getPrice());
        assertEquals(700.0, result.getContent().get(1).getPrice());
        assertEquals(500.0, result.getContent().get(2).getPrice());
    }
    @Test
    void getProductsByPriceRange_preservesProductOrder() {

        Product product1 = new Product();
        product1.setId(3L);
        product1.setName("Keyboard");
        product1.setPrice(500.0);
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(1L);
        product2.setName("Laptop");
        product2.setPrice(900.0);
        product2.setQuantity(5);

        Product product3 = new Product();
        product3.setId(2L);
        product3.setName("Monitor");
        product3.setPrice(700.0);
        product3.setQuantity(3);

        Page<Product> productPage =
                new PageImpl<>(List.of(product1, product2, product3));

        when(productRepository.findByPriceBetween(
                eq(500.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        500.0,
                        1000.0,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertEquals(3L, result.getContent().get(0).getId());
        assertEquals(1L, result.getContent().get(1).getId());
        assertEquals(2L, result.getContent().get(2).getId());
    }
    @Test
    void getProductsByPriceRange_emptyPage_preservesPaginationMetadata() {

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(
                                1,
                                5,
                                Sort.by(Sort.Direction.ASC, "price")
                        ),
                        10
                );

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByPriceRange(
                        100.0,
                        1000.0,
                        1,
                        5,
                        "price",
                        "asc"
                );

        assertTrue(result.isEmpty());
        assertEquals(1, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(10, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }
    @Test
    void getProductsByPriceRange_repositoryCalledExactlyOnce() {

        Page<Product> productPage = new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        )).thenReturn(productPage);

        productService.getProductsByPriceRange(
                100.0,
                1000.0,
                0,
                5,
                "id",
                "asc"
        );

        verify(productRepository, times(1)).findByPriceBetween(
                eq(100.0),
                eq(1000.0),
                any(Pageable.class)
        );

        verifyNoMoreInteractions(productRepository);
    }
    @Test
    void getProductsByCategory_categoryNotFound() {

        Long categoryId = 999L;

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception =
                assertThrows(
                        CategoryNotFoundException.class,
                        () -> productService.getProductsByCategory(
                                categoryId,
                                0,
                                5,
                                "id",
                                "asc"
                        )
                );

        assertEquals(
                "Category not found with id : " + categoryId,
                exception.getMessage()
        );

        verify(categoryRepository)
                .findById(categoryId);

        verify(productRepository, never())
                .findByCategoryId(
                        anyLong(),
                        any(Pageable.class)
                );
    }
    @Test
    void searchProductsByName_descendingSort_success() {

        String name = "Laptop";

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Test Laptop");
        product.setPrice(75000.0);
        product.setQuantity(10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "price")),
                        1);

        when(productRepository.findByNameContainingIgnoreCase(
                eq(name),
                any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.searchProductsByName(
                        name,
                        0,
                        5,
                        "price",
                        "desc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());

        verify(productRepository)
                .findByNameContainingIgnoreCase(
                        eq(name),
                        any(Pageable.class));
    }
    @Test
    void getAllProducts_descendingSort_success() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Test Laptop");
        product.setPrice(75000.0);
        product.setQuantity(10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "price")),
                        1);

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getAllProducts(
                        0,
                        5,
                        "price",
                        "desc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(
                "Laptop",
                result.getContent().get(0).getName());

        verify(productRepository)
                .findAll(any(Pageable.class));
    }
    @Test
    void updateProduct_withCategory_success() {

        Long productId = 1L;
        Long categoryId = 10L;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Product");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(50000.0);
        existingProduct.setQuantity(5);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(60000.0);
        request.setQuantity(10);
        request.setCategoryId(categoryId);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        ProductResponse response =
                productService.updateProduct(productId, request);

        assertNotNull(response);

        assertEquals("Updated Product", response.getName());
        assertEquals("Updated Description", response.getDescription());
        assertEquals(60000.0, response.getPrice());
        assertEquals(10, response.getQuantity());

        assertEquals(categoryId, response.getCategoryId());
        assertEquals("Electronics", response.getCategoryName());

        verify(productRepository).findById(productId);
        verify(categoryRepository).findById(categoryId);
        verify(productRepository).save(existingProduct);
    }
    @Test
    void getProductsByCategory_descendingSort_success() {

        Long categoryId = 10L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Test Laptop");
        product.setPrice(75000.0);
        product.setQuantity(10);
        product.setCategory(category);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product),
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "price")),
                        1);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.findByCategoryId(
                eq(categoryId),
                any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.getProductsByCategory(
                        categoryId,
                        0,
                        5,
                        "price",
                        "desc");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(
                "Laptop",
                result.getContent().get(0).getName());

        verify(categoryRepository)
                .findById(categoryId);

        verify(productRepository)
                .findByCategoryId(
                        eq(categoryId),
                        any(Pageable.class));
    }
    @Test
    void updateProduct_categoryNotFound() {

        Long productId = 1L;
        Long categoryId = 999L;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Product");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(50000.0);
        existingProduct.setQuantity(5);

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(60000.0);
        request.setQuantity(10);
        request.setCategoryId(categoryId);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception =
                assertThrows(
                        CategoryNotFoundException.class,
                        () -> productService.updateProduct(
                                productId,
                                request
                        )
                );

        assertEquals(
                "Category not found with id : " + categoryId,
                exception.getMessage()
        );

        verify(productRepository)
                .findById(productId);

        verify(categoryRepository)
                .findById(categoryId);

        verify(productRepository, never())
                .save(any(Product.class));
    }
    @Test
    void filterProducts_shouldHandleMinPriceWithoutMaxPrice() {

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.filterProducts(
                        50000.0,
                        null,
                        null,
                        null,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository)
                .findAll(
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "id"
                                )
                        )
                );
    }
    @Test
    void filterProducts_shouldHandleMinQuantityWithoutMaxQuantity() {

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findAll(any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.filterProducts(
                        null,
                        null,
                        5,
                        null,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository)
                .findAll(
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "id"
                                )
                        )
                );
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenSortFieldIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                null,
                                null,
                                0,
                                5,
                                null,
                                "asc"
                        )
                );

        assertEquals(
                "Invalid sort field: null",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldThrowExceptionWhenSortDirectionIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> productService.filterProducts(
                                null,
                                null,
                                null,
                                null,
                                0,
                                5,
                                "id",
                                null
                        )
                );

        assertEquals(
                "Invalid sort direction: null. Use 'asc' or 'desc'",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }
    @Test
    void filterProducts_shouldHandleMinQuantityWithPriceRangeButWithoutMaxQuantity() {

        Page<Product> productPage =
                new PageImpl<>(List.of());

        when(productRepository.findByPriceBetween(
                eq(50000.0),
                eq(100000.0),
                any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> result =
                productService.filterProducts(
                        50000.0,
                        100000.0,
                        5,
                        null,
                        0,
                        5,
                        "id",
                        "asc"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository)
                .findByPriceBetween(
                        eq(50000.0),
                        eq(100000.0),
                        any(Pageable.class));

        verify(productRepository, never())
                .findByPriceBetweenAndQuantityBetween(
                        anyDouble(),
                        anyDouble(),
                        anyInt(),
                        anyInt(),
                        any(Pageable.class));
    }

}