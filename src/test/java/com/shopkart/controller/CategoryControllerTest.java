package com.shopkart.controller;

import com.shopkart.entity.Category;
import com.shopkart.exception.CategoryInUseException;
import com.shopkart.exception.CategoryNotFoundException;
import com.shopkart.exception.GlobalExceptionHandler;
import com.shopkart.service.CategoryService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.jupiter.api.Test;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;
    @Test
    void createCategory_success() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.createCategory(any(Category.class)))
                .thenReturn(category);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Electronics"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"));

        verify(categoryService, times(1))
                .createCategory(any(Category.class));
    }
    @Test
    void createCategory_validationFailure() throws Exception {

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": ""
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists());

        verifyNoInteractions(categoryService);
    }
    @Test
    void getAllCategories_success() throws Exception {

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Electronics");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Clothing");

        when(categoryService.getAllCategories())
                .thenReturn(java.util.List.of(category1, category2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Electronics"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Clothing"));

        verify(categoryService, times(1))
                .getAllCategories();
    }
    @Test
    void getAllCategories_empty() throws Exception {

        when(categoryService.getAllCategories())
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(categoryService, times(1))
                .getAllCategories();
    }
    @Test
    void getCategoryById_success() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.getCategoryById(1L))
                .thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"));

        verify(categoryService, times(1))
                .getCategoryById(1L);
    }
    @Test
    void getCategoryById_notFound() throws Exception {

        when(categoryService.getCategoryById(999L))
                .thenThrow(new CategoryNotFoundException(
                        "Category not found with id: 999"
                ));

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Category not found with id: 999"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(categoryService, times(1))
                .getCategoryById(999L);
    }
    @Test
    void getCategoryById_invalidId() throws Exception {

        Category category = new Category();
        category.setId(-1L);
        category.setName("Invalid ID Category");

        when(categoryService.getCategoryById(-1L))
                .thenReturn(category);

        mockMvc.perform(get("/api/categories/-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(-1))
                .andExpect(jsonPath("$.data.name").value("Invalid ID Category"));

        verify(categoryService, times(1))
                .getCategoryById(-1L);
    }
    @Test
    void updateCategory_success() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.updateCategory(eq(1L), any(Category.class)))
                .thenReturn(category);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Electronics"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"));

        verify(categoryService, times(1))
                .updateCategory(eq(1L), any(Category.class));
    }
    @Test
    void updateCategory_notFound() throws Exception {

        when(categoryService.updateCategory(eq(999L), any(Category.class)))
                .thenThrow(new CategoryNotFoundException(
                        "Category not found with id: 999"
                ));

        mockMvc.perform(put("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Electronics"
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Category not found with id: 999"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(categoryService, times(1))
                .updateCategory(eq(999L), any(Category.class));
    }
    @Test
    void updateCategory_validationFailure() throws Exception {

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": ""
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists());

        verifyNoInteractions(categoryService);
    }
    @Test
    void updateCategory_invalidId() throws Exception {

        Category category = new Category();
        category.setId(-1L);
        category.setName("Electronics");

        when(categoryService.updateCategory(eq(-1L), any(Category.class)))
                .thenReturn(category);

        mockMvc.perform(put("/api/categories/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Electronics"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(-1))
                .andExpect(jsonPath("$.data.name").value("Electronics"));

        verify(categoryService, times(1))
                .updateCategory(eq(-1L), any(Category.class));
    }
    @Test
    void deleteCategory_success() throws Exception {

        when(categoryService.deleteCategory(1L))
                .thenReturn("Category deleted successfully");

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Category deleted successfully"))
                .andExpect(jsonPath("$.data")
                        .value("Category deleted successfully"));

        verify(categoryService, times(1))
                .deleteCategory(1L);
    }
    @Test
    void deleteCategory_notFound() throws Exception {

        when(categoryService.deleteCategory(999L))
                .thenThrow(new CategoryNotFoundException(
                        "Category not found with id: 999"
                ));

        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Category not found with id: 999"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(categoryService, times(1))
                .deleteCategory(999L);
    }
    @Test
    void deleteCategory_inUse() throws Exception {

        when(categoryService.deleteCategory(1L))
                .thenThrow(new CategoryInUseException(
                        "Category cannot be deleted because it is in use"
                ));

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Category cannot be deleted because it is in use"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(categoryService, times(1))
                .deleteCategory(1L);
    }
    @Test
    void deleteCategory_serviceInteraction() throws Exception {

        when(categoryService.deleteCategory(1L))
                .thenReturn("Category deleted successfully");

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk());

        verify(categoryService, times(1))
                .deleteCategory(1L);

        verifyNoMoreInteractions(categoryService);
    }
    @Test
    void createCategory_serviceInteraction() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.createCategory(any(Category.class)))
                .thenReturn(category);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Electronics"
                            }
                            """))
                .andExpect(status().isCreated());

        verify(categoryService, times(1))
                .createCategory(any(Category.class));

        verifyNoMoreInteractions(categoryService);
    }
    @Test
    void getAllCategories_serviceInteraction() throws Exception {

        when(categoryService.getAllCategories())
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());

        verify(categoryService, times(1))
                .getAllCategories();

        verifyNoMoreInteractions(categoryService);
    }
    @Test
    void getCategoryById_serviceInteraction() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.getCategoryById(1L))
                .thenReturn(category);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk());

        verify(categoryService, times(1))
                .getCategoryById(1L);

        verifyNoMoreInteractions(categoryService);
    }
    @Test
    void updateCategory_serviceInteraction() throws Exception {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryService.updateCategory(eq(1L), any(Category.class)))
                .thenReturn(category);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Electronics"
                            }
                            """))
                .andExpect(status().isOk());

        verify(categoryService, times(1))
                .updateCategory(eq(1L), any(Category.class));

        verifyNoMoreInteractions(categoryService);
    }
    @Test
    void createCategory_blankName() throws Exception {

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "   "
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists());

        verifyNoInteractions(categoryService);
    }
    @Test
    void createCategory_nullName() throws Exception {

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": null
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists());

        verifyNoInteractions(categoryService);
    }
    @Test
    void updateCategory_blankName() throws Exception {

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "   "
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.name").exists());

        verifyNoInteractions(categoryService);
    }
    @Test
    void createCategory_malformedJson() throws Exception {

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"name":
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }
    @Test
    void unsupportedHttpMethod() throws Exception {

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/api/categories/1")
                )
                .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(categoryService);
    }
    @Test
    void exceptionHandling_categoryNotFound() throws Exception {

        when(categoryService.getCategoryById(999L))
                .thenThrow(new CategoryNotFoundException(
                        "Category not found with id: 999"));

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Category not found with id: 999"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(categoryService, times(1))
                .getCategoryById(999L);

        verifyNoMoreInteractions(categoryService);
    }
}