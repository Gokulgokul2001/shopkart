package com.shopkart.controller;

import com.shopkart.dto.ProductUpdateRequest;
import com.shopkart.exception.CategoryNotFoundException;
import com.shopkart.exception.ProductNotFoundException;
import tools.jackson.databind.ObjectMapper;
import com.shopkart.dto.ProductRequest;
import com.shopkart.dto.ProductResponse;
import com.shopkart.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;
@WebMvcTest(ProductController.class)
@Import(com.shopkart.exception.GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_success() throws Exception {

        ProductRequest request = new ProductRequest();

        request.setName("Laptop");
        request.setDescription("Gaming Laptop");
        request.setPrice(75000.0);
        request.setQuantity(10);
        request.setCategoryId(1L);

        ProductResponse response = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                75000.0,
                10,
                1L,
                "Electronics"
        );

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message")
                        .value("Product created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Laptop"))
                .andExpect(jsonPath("$.data.description")
                        .value("Gaming Laptop"))
                .andExpect(jsonPath("$.data.price").value(75000.0))
                .andExpect(jsonPath("$.data.quantity").value(10))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.categoryName")
                        .value("Electronics"));
    }
    @Test
    void createProduct_validationError() throws Exception {

        ProductRequest request = new ProductRequest();

        // Invalid request
        request.setName("");
        request.setDescription("");
        request.setPrice(-100.0);
        request.setQuantity(-5);
        request.setCategoryId(1L);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())

                // ApiResponse
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))

                // Validation errors
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.description").exists())
                .andExpect(jsonPath("$.data.price").exists())
                .andExpect(jsonPath("$.data.quantity").exists());
    }
    @Test
    void getProductById_success() throws Exception {

        ProductResponse response = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                75000.0,
                10,
                1L,
                "Electronics"
        );

        when(productService.getProductById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/products/1")
                )
                .andExpect(status().isOk())

                // ApiResponse
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Product retrieved successfully"))

                // ProductResponse
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Laptop"))
                .andExpect(jsonPath("$.data.description")
                        .value("Gaming Laptop"))
                .andExpect(jsonPath("$.data.price").value(75000.0))
                .andExpect(jsonPath("$.data.quantity").value(10))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.categoryName")
                        .value("Electronics"));
    }
    @Test
    void getProductById_notFound() throws Exception {

        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException(
                        "Product not found with id: 999"
                ));

        mockMvc.perform(
                        get("/api/products/999")
                )
                .andExpect(status().isNotFound())

                // ApiResponse
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with id: 999"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
    @Test
    void updateProduct_success() throws Exception {

        ProductRequest request = new ProductRequest();
        request.setName("Updated Laptop");
        request.setDescription("Updated Gaming Laptop");
        request.setPrice(85000.0);
        request.setQuantity(15);
        request.setCategoryId(1L);

        ProductResponse response = new ProductResponse(
                1L,
                "Updated Laptop",
                "Updated Gaming Laptop",
                85000.0,
                15,
                1L,
                "Electronics"
        );

        when(productService.updateProduct(
                any(Long.class),
                any(ProductUpdateRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Product updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Updated Laptop"))
                .andExpect(jsonPath("$.data.description")
                        .value("Updated Gaming Laptop"))
                .andExpect(jsonPath("$.data.price").value(85000.0))
                .andExpect(jsonPath("$.data.quantity").value(15))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.categoryName")
                        .value("Electronics"));
    }
    @Test
    void updateProduct_validationError() throws Exception {

        ProductUpdateRequest request = new ProductUpdateRequest();

        request.setName("");
        request.setDescription("");
        request.setPrice(-100.0);
        request.setQuantity(-5);
        request.setCategoryId(1L);

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.description").exists())
                .andExpect(jsonPath("$.data.price").exists())
                .andExpect(jsonPath("$.data.quantity").exists());
    }
    @Test
    void deleteProduct_success() throws Exception {

        when(productService.deleteProduct(1L))
                .thenReturn("Product deleted successfully");

        mockMvc.perform(
                        delete("/api/products/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message")
                        .value("Product deleted successfully"))
                .andExpect(jsonPath("$.message")
                        .value("Product deleted successfully"));
    }

    @Test
    void deleteProduct_notFound() throws Exception {

        when(productService.deleteProduct(999L))
                .thenThrow(new ProductNotFoundException(
                        "Product not found with id: 999"
                ));

        mockMvc.perform(
                        delete("/api/products/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Product not found with id: 999"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
    @Test
    void getAllProducts_success() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                75000.0,
                10,
                1L,
                "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L,
                "Mouse",
                "Wireless Mouse",
                1500.0,
                20,
                1L,
                "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.getAllProducts(
                0, 5, "id", "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.data.content[1].id").value(2))
                .andExpect(jsonPath("$.data.content[1].name").value("Mouse"));
    }
    @Test
    void getAllProducts_paginationAndSorting() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                75000.0,
                10,
                1L,
                "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L,
                "Mouse",
                "Wireless Mouse",
                1500.0,
                20,
                1L,
                "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.getAllProducts(
                1, 10, "price", "desc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products")
                                .param("page", "1")
                                .param("size", "10")
                                .param("sortBy", "price")
                                .param("direction", "desc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[1].id").value(2));
    }
    @Test
    void searchProductsByName_success() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                75000.0,
                10,
                1L,
                "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L,
                "Laptop Stand",
                "Adjustable Laptop Stand",
                2500.0,
                15,
                1L,
                "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.searchProductsByName(
                "Laptop", 0, 5, "id", "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products/search")
                                .param("name", "Laptop")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name")
                        .value("Laptop"))
                .andExpect(jsonPath("$.data.content[1].name")
                        .value("Laptop Stand"));
    }
    @Test
    void searchProductsByName_paginationAndSorting() throws Exception {

        ProductResponse product = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                75000.0,
                10,
                1L,
                "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product));

        when(productService.searchProductsByName(
                "Laptop", 1, 10, "price", "desc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products/search")
                                .param("name", "Laptop")
                                .param("page", "1")
                                .param("size", "10")
                                .param("sortBy", "price")
                                .param("direction", "desc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name")
                        .value("Laptop"))
                .andExpect(jsonPath("$.data.content[0].price")
                        .value(75000.0));
    }
    @Test
    void getProductsByCategory_success() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                75000.0,
                10,
                1L,
                "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L,
                "Mouse",
                "Wireless Mouse",
                1500.0,
                20,
                1L,
                "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.getProductsByCategory(
                1L, 0, 5, "id", "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products/category/1")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].categoryId").value(1))
                .andExpect(jsonPath("$.data.content[0].categoryName")
                        .value("Electronics"))
                .andExpect(jsonPath("$.data.content[1].categoryId").value(1))
                .andExpect(jsonPath("$.data.content[1].categoryName")
                        .value("Electronics"));
    }
    @Test
    void getProductsByCategory_notFound() throws Exception {

        when(productService.getProductsByCategory(
                999L, 0, 5, "id", "asc"
        )).thenThrow(new CategoryNotFoundException(
                "Category not found with id: 999"
        ));

        mockMvc.perform(
                        get("/api/products/category/999")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Category not found with id: 999"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
    @Test
    void filterProducts_combinedFilters() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L, "Laptop", "Gaming Laptop",
                75000.0, 10, 1L, "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L, "Monitor", "4K Monitor",
                100000.0, 8, 1L, "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.filterProducts(
                50000.0,
                150000.0,
                5,
                10,
                0,
                10,
                "id",
                "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products/filter")
                                .param("minPrice", "50000")
                                .param("maxPrice", "150000")
                                .param("minQuantity", "5")
                                .param("maxQuantity", "10")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.data.content[1].name").value("Monitor"));
    }
    @Test
    void filterProducts_priceOnly() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L, "Laptop", "Gaming Laptop",
                80000.0, 10, 1L, "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L, "Monitor", "4K Monitor",
                90000.0, 8, 1L, "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.filterProducts(
                80000.0,
                100000.0,
                null,
                null,
                0,
                10,
                "id",
                "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products/filter")
                                .param("minPrice", "80000")
                                .param("maxPrice", "100000")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].price").value(80000.0))
                .andExpect(jsonPath("$.data.content[1].price").value(90000.0));
    }
    @Test
    void filterProducts_quantityOnly() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L, "Laptop", "Gaming Laptop",
                75000.0, 5, 1L, "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L, "Mouse", "Wireless Mouse",
                1500.0, 8, 1L, "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.filterProducts(
                null,
                null,
                5,
                10,
                0,
                10,
                "id",
                "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products/filter")
                                .param("minQuantity", "5")
                                .param("maxQuantity", "10")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].quantity").value(5))
                .andExpect(jsonPath("$.data.content[1].quantity").value(8));
    }
    @Test
    void filterProducts_invalidPriceRange() throws Exception {

        when(productService.filterProducts(
                150000.0,
                50000.0,
                null,
                null,
                0,
                10,
                "id",
                "asc"
        )).thenThrow(new IllegalArgumentException(
                "Minimum price cannot be greater than maximum price"
        ));

        mockMvc.perform(
                        get("/api/products/filter")
                                .param("minPrice", "150000")
                                .param("maxPrice", "50000")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Minimum price cannot be greater than maximum price"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
    @Test
    void getProductsByPriceRange_success() throws Exception {

        ProductResponse product1 = new ProductResponse(
                1L,
                "Laptop",
                "Gaming Laptop",
                80000.0,
                10,
                1L,
                "Electronics"
        );

        ProductResponse product2 = new ProductResponse(
                2L,
                "Monitor",
                "4K Monitor",
                90000.0,
                8,
                1L,
                "Electronics"
        );

        Page<ProductResponse> page =
                new PageImpl<>(List.of(product1, product2));

        when(productService.getProductsByPriceRange(
                80000.0,
                100000.0,
                0,
                5,
                "id",
                "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/products/price")
                                .param("minPrice", "80000")
                                .param("maxPrice", "100000")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name")
                        .value("Laptop"))
                .andExpect(jsonPath("$.data.content[0].price")
                        .value(80000.0))
                .andExpect(jsonPath("$.data.content[1].name")
                        .value("Monitor"))
                .andExpect(jsonPath("$.data.content[1].price")
                        .value(90000.0));
    }
    @Test
    void getProductsByPriceRange_invalidRange() throws Exception {

        when(productService.getProductsByPriceRange(
                150000.0,
                50000.0,
                0,
                5,
                "id",
                "asc"
        )).thenThrow(new IllegalArgumentException(
                "Minimum price cannot be greater than maximum price"
        ));

        mockMvc.perform(
                        get("/api/products/price")
                                .param("minPrice", "150000")
                                .param("maxPrice", "50000")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Minimum price cannot be greater than maximum price"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
    @Test
    void getAllProducts_invalidPage() throws Exception {

        mockMvc.perform(
                        get("/api/products")
                                .param("page", "-1")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.data['getAllProducts.page']").exists());
    }
    @Test
    void getAllProducts_invalidSortField() throws Exception {

        when(productService.getAllProducts(
                0,
                5,
                "invalidField",
                "asc"
        )).thenThrow(new IllegalArgumentException(
                "Invalid sort field: invalidField"
        ));

        mockMvc.perform(
                        get("/api/products")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "invalidField")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid sort field: invalidField"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
    @Test
    void getAllProducts_invalidSortDirection() throws Exception {

        when(productService.getAllProducts(
                0,
                5,
                "id",
                "invalid"
        )).thenThrow(new IllegalArgumentException(
                "Invalid sort direction: invalid"
        ));

        mockMvc.perform(
                        get("/api/products")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "invalid")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid sort direction: invalid"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

}