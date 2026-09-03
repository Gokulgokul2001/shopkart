package com.shopkart.integration;

import com.shopkart.entity.Product;
import tools.jackson.databind.ObjectMapper;
import com.shopkart.dto.ProductRequest;
import com.shopkart.entity.Category;
import com.shopkart.repository.CategoryRepository;
import com.shopkart.repository.ProductRepository;
import com.shopkart.dto.ProductUpdateRequest;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createProduct_success() throws Exception {

        Category category = new Category();
        category.setName("IntegrationTestCategory1");

        Category savedCategory =
                categoryRepository.save(category);

        ProductRequest request = new ProductRequest();

        request.setName("IntegrationTestProduct1");
        request.setDescription("Integration Test Product");
        request.setPrice(60000.0);
        request.setQuantity(10);
        request.setCategoryId(savedCategory.getId());

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.name")
                        .value("IntegrationTestProduct1"))
                .andExpect(jsonPath("$.data.description")
                        .value("Integration Test Product"))
                .andExpect(jsonPath("$.data.price")
                        .value(60000.0))
                .andExpect(jsonPath("$.data.quantity")
                        .value(10))
                .andExpect(jsonPath("$.data.categoryId")
                        .value(savedCategory.getId()));

        long count =
                productRepository.count();

        assertTrue(count > 0);
    }
    @Test
    void getProductById_success() throws Exception {

        Product product = new Product();
        product.setName("IntegrationGetProduct2");
        product.setDescription("Get Product Integration Test");
        product.setPrice(55000.0);
        product.setQuantity(15);

        Product savedProduct =
                productRepository.save(product);

        mockMvc.perform(
                        get("/api/products/" + savedProduct.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id")
                        .value(savedProduct.getId()))
                .andExpect(jsonPath("$.data.name")
                        .value("IntegrationGetProduct2"))
                .andExpect(jsonPath("$.data.description")
                        .value("Get Product Integration Test"))
                .andExpect(jsonPath("$.data.price")
                        .value(55000.0))
                .andExpect(jsonPath("$.data.quantity")
                        .value(15));
    }
    @Test
    void getAllProducts_success() throws Exception {

        Product product1 = new Product();
        product1.setName("IntegrationAllProduct3A");
        product1.setDescription("Get All Test Product A");
        product1.setPrice(40000.0);
        product1.setQuantity(5);

        Product product2 = new Product();
        product2.setName("IntegrationAllProduct3B");
        product2.setDescription("Get All Test Product B");
        product2.setPrice(50000.0);
        product2.setQuantity(10);

        productRepository.save(product1);
        productRepository.save(product2);

        mockMvc.perform(
                        get("/api/products")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andExpect(jsonPath("$.data.size").exists());
    }
    @Test
    void updateProduct_success() throws Exception {

        Product product = new Product();
        product.setName("IntegrationUpdateProduct4");
        product.setDescription("Original Description");
        product.setPrice(40000.0);
        product.setQuantity(5);

        Product savedProduct =
                productRepository.save(product);

        ProductUpdateRequest request =
                new ProductUpdateRequest();

        request.setName("IntegrationUpdatedProduct4");
        request.setDescription("Updated Description");
        request.setPrice(65000.0);
        request.setQuantity(20);

        mockMvc.perform(
                        put("/api/products/" + savedProduct.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id")
                        .value(savedProduct.getId()))
                .andExpect(jsonPath("$.data.name")
                        .value("IntegrationUpdatedProduct4"))
                .andExpect(jsonPath("$.data.description")
                        .value("Updated Description"))
                .andExpect(jsonPath("$.data.price")
                        .value(65000.0))
                .andExpect(jsonPath("$.data.quantity")
                        .value(20));
    }
    @Test
    void deleteProduct_success() throws Exception {

        Product product = new Product();
        product.setName("IntegrationDeleteProduct5");
        product.setDescription("Delete Integration Test");
        product.setPrice(35000.0);
        product.setQuantity(5);

        Product savedProduct =
                productRepository.save(product);

        Long productId = savedProduct.getId();

        mockMvc.perform(
                        delete("/api/products/" + productId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").doesNotExist());

        assertFalse(
                productRepository.findById(productId).isPresent()
        );
    }
    @Test
    void searchProductsByName_success() throws Exception {

        Product product = new Product();
        product.setName("IntegrationSearchProduct6");
        product.setDescription("Search Integration Test");
        product.setPrice(45000.0);
        product.setQuantity(8);

        productRepository.save(product);

        mockMvc.perform(
                        get("/api/products/search")
                                .param("name", "integrationsearchproduct6")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andExpect(jsonPath("$.data.content[0].name")
                        .value("IntegrationSearchProduct6"))
                .andExpect(jsonPath("$.data.content[0].price")
                        .value(45000.0))
                .andExpect(jsonPath("$.data.content[0].quantity")
                        .value(8));
    }
    @Test
    void getProductsByCategory_success() throws Exception {

        Category category = new Category();
        category.setName("IntegrationCategory7");

        Category savedCategory =
                categoryRepository.save(category);

        Product product = new Product();
        product.setName("IntegrationCategoryProduct7");
        product.setDescription("Category Integration Test");
        product.setPrice(55000.0);
        product.setQuantity(12);
        product.setCategory(savedCategory);

        productRepository.save(product);

        mockMvc.perform(
                        get("/api/products/category/" + savedCategory.getId())
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andExpect(jsonPath("$.data.content[0].name")
                        .value("IntegrationCategoryProduct7"))
                .andExpect(jsonPath("$.data.content[0].categoryId")
                        .value(savedCategory.getId()));
    }
    @Test
    void filterProducts_success() throws Exception {

        Product product = new Product();
        product.setName("IntegrationFilterProduct8");
        product.setDescription("Filter Integration Test");
        product.setPrice(85000.0);
        product.setQuantity(8);

        productRepository.save(product);

        mockMvc.perform(
                        get("/api/products/filter")
                                .param("minPrice", "80000")
                                .param("maxPrice", "90000")
                                .param("minQuantity", "5")
                                .param("maxQuantity", "10")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty());

    }
    @Test
    void getProductsByPriceRange_success() throws Exception {

        Product product = new Product();
        product.setName("IntegrationPriceProduct9");
        product.setDescription("Price Range Integration Test");
        product.setPrice(75000.0);
        product.setQuantity(10);

        productRepository.save(product);

        mockMvc.perform(
                        get("/api/products/price")
                                .param("minPrice", "70000")
                                .param("maxPrice", "80000")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isNotEmpty());
    }
    @Test
    void createProduct_validationFailure() throws Exception {

        String invalidRequest = """
            {
                "name": "",
                "description": "",
                "price": -100,
                "quantity": -5
            }
            """;

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.data").exists());
    }
}