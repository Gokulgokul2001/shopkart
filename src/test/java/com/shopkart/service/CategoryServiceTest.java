package com.shopkart.service;

import com.shopkart.entity.Category;
import com.shopkart.exception.CategoryInUseException;
import com.shopkart.exception.CategoryNotFoundException;
import com.shopkart.repository.CategoryRepository;
import com.shopkart.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_success() {

        Category category = new Category();
        category.setName("Electronics");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Electronics");

        when(categoryRepository.save(category))
                .thenReturn(savedCategory);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());

        verify(categoryRepository, times(1)).save(category);
    }
    @Test
    void getCategoryById_success() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());

        verify(categoryRepository, times(1)).findById(1L);
    }
    @Test
    void getCategoryById_notFound() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(999L)
        );

        verify(categoryRepository, times(1))
                .findById(999L);
    }
    @Test
    void getAllCategories_success() {

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Electronics");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Clothing");

        when(categoryRepository.findAll())
                .thenReturn(List.of(category1, category2));

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("Electronics", result.get(0).getName());

        assertEquals(2L, result.get(1).getId());
        assertEquals("Clothing", result.get(1).getName());

        verify(categoryRepository, times(1)).findAll();
    }
    @Test
    void getAllCategories_empty() {

        when(categoryRepository.findAll())
                .thenReturn(List.of());

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryRepository, times(1)).findAll();
    }
    @Test
    void updateCategory_success() {

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Mobile Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.save(existingCategory))
                .thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mobile Electronics", result.getName());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(existingCategory);
    }
    @Test
    void updateCategory_notFound() {

        Category updatedCategory = new Category();
        updatedCategory.setName("Mobile Electronics");

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.updateCategory(999L, updatedCategory)
        );

        verify(categoryRepository, times(1))
                .findById(999L);

        verify(categoryRepository, never())
                .save(any(Category.class));
    }
    @Test
    void updateCategory_success_updatesExistingCategory() {

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Electronics");

        Category updateRequest = new Category();
        updateRequest.setName("Mobile");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.save(existingCategory))
                .thenReturn(existingCategory);

        Category result = categoryService.updateCategory(1L, updateRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mobile", result.getName());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(existingCategory);
    }
    @Test
    void deleteCategory_success() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.countByCategoryId(1L))
                .thenReturn(0L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .countByCategoryId(1L);

        verify(categoryRepository, times(1))
                .delete(category);
    }
    @Test
    void deleteCategory_notFound() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(999L)
        );

        verify(categoryRepository, times(1))
                .findById(999L);

        verify(productRepository, never())
                .countByCategoryId(anyLong());

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }
    @Test
    void deleteCategory_inUse() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.countByCategoryId(1L))
                .thenReturn(3L);

        assertThrows(
                CategoryInUseException.class,
                () -> categoryService.deleteCategory(1L)
        );

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .countByCategoryId(1L);

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }
    @Test
    void deleteCategory_checksProductCountBeforeDelete() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.countByCategoryId(1L))
                .thenReturn(0L);

        categoryService.deleteCategory(1L);

        verify(productRepository, times(1))
                .countByCategoryId(1L);

        verify(categoryRepository, times(1))
                .delete(category);
    }
    @Test
    void deleteCategory_inUseWithOneProduct() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.countByCategoryId(1L))
                .thenReturn(1L);

        assertThrows(
                CategoryInUseException.class,
                () -> categoryService.deleteCategory(1L)
        );

        verify(productRepository, times(1))
                .countByCategoryId(1L);

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }
    @Test
    void deleteCategory_inUseWithMultipleProducts() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.countByCategoryId(1L))
                .thenReturn(10L);

        assertThrows(
                CategoryInUseException.class,
                () -> categoryService.deleteCategory(1L)
        );

        verify(productRepository, times(1))
                .countByCategoryId(1L);

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }
    @Test
    void createCategory_repositoryInteraction() {

        Category category = new Category();
        category.setName("Books");

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("Books", result.getName());

        verify(categoryRepository, times(1))
                .save(category);

        verifyNoInteractions(productRepository);
    }
    @Test
    void getCategoryById_repositoryInteraction() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Books");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Books", result.getName());

        verify(categoryRepository, times(1))
                .findById(1L);

        verifyNoInteractions(productRepository);
    }
    @Test
    void getAllCategories_repositoryInteraction() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Books");

        when(categoryRepository.findAll())
                .thenReturn(List.of(category));

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(categoryRepository, times(1))
                .findAll();

        verifyNoInteractions(productRepository);
    }
    @Test
    void updateCategory_repositoryInteraction() {

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Books");

        Category updateRequest = new Category();
        updateRequest.setName("Stationery");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.save(existingCategory))
                .thenReturn(existingCategory);

        Category result =
                categoryService.updateCategory(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Stationery", result.getName());

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(categoryRepository, times(1))
                .save(existingCategory);

        verifyNoInteractions(productRepository);
    }
    @Test
    void createCategory_doesNotInteractWithProductRepository() {

        Category category = new Category();
        category.setName("Books");

        when(categoryRepository.save(category))
                .thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("Books", result.getName());

        verify(categoryRepository, times(1))
                .save(category);

        verifyNoInteractions(productRepository);
    }
    @Test
    void updateCategory_doesNotInteractWithProductRepository() {

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Books");

        Category updateRequest = new Category();
        updateRequest.setName("Stationery");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.save(existingCategory))
                .thenReturn(existingCategory);

        Category result =
                categoryService.updateCategory(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Stationery", result.getName());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(existingCategory);

        verifyNoInteractions(productRepository);
    }
    @Test
    void deleteCategory_repositoryInteraction() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Books");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.countByCategoryId(1L))
                .thenReturn(0L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .countByCategoryId(1L);

        verify(categoryRepository, times(1))
                .delete(category);
    }
    @Test
    void deleteCategory_notFound_doesNotCheckProductCount() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(999L)
        );

        verify(categoryRepository, times(1))
                .findById(999L);

        verifyNoInteractions(productRepository);

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }
    @Test
    void createCategory_nullCategory() {

        Category result = categoryService.createCategory(null);

        assertNull(result);

        verify(categoryRepository, times(1))
                .save(null);

        verifyNoInteractions(productRepository);
    }
    @Test
    void getCategoryById_nullId() {

        assertThrows(
                Exception.class,
                () -> categoryService.getCategoryById(null)
        );

        verify(categoryRepository, times(1))
                .findById(null);

        verifyNoInteractions(productRepository);
    }
    @Test
    void updateCategory_nullId() {

        Category category = new Category();
        category.setName("Electronics");

        assertThrows(
                Exception.class,
                () -> categoryService.updateCategory(null, category)
        );

        verify(categoryRepository, times(1))
                .findById(null);

        verifyNoInteractions(productRepository);
    }
}