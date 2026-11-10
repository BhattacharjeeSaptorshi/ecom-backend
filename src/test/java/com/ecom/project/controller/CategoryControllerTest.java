package com.ecom.project.controller;

import com.ecom.project.payload.CategoryDTO;
import com.ecom.project.payload.CategoryResponse;
import com.ecom.project.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryControllerTest {

    private CategoryService categoryService;
    private CategoryController controller;

    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        controller = new CategoryController();
        ReflectionTestUtils.setField(controller, "categoryService", categoryService);
    }

    @Test
    void getAllCategoriesReturnsOkAndDelegatesPagination() {
        CategoryResponse categories = new CategoryResponse();
        when(categoryService.getAllCategories(1, 10, "categoryName", "desc")).thenReturn(categories);

        var response = controller.getAllCategories(1, 10, "categoryName", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(categories, response.getBody());
        verify(categoryService).getAllCategories(1, 10, "categoryName", "desc");
    }

    @Test
    void getCategoryReturnsOkAndDelegates() {
        CategoryDTO category = new CategoryDTO(2L, "Shoes");
        when(categoryService.getCategory(2L)).thenReturn(category);

        var response = controller.getCategory(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(category, response.getBody());
        verify(categoryService).getCategory(2L);
    }

    @Test
    void getCategoryReturnsServiceStatusWhenNotFound() {
        when(categoryService.getCategory(2L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        var response = controller.getCategory(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createCategoryReturnsCreatedAndDelegates() {
        CategoryDTO request = new CategoryDTO(null, "Shoes");
        CategoryDTO saved = new CategoryDTO(3L, "Shoes");
        when(categoryService.createCategory(request)).thenReturn(saved);

        var response = controller.createCategory(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
        verify(categoryService).createCategory(request);
    }

    @Test
    void deleteCategoryReturnsOkAndDelegates() {
        CategoryDTO deleted = new CategoryDTO(3L, "Shoes");
        when(categoryService.deleteCategory(3L)).thenReturn(deleted);

        var response = controller.deleteCategory(3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(deleted, response.getBody());
        verify(categoryService).deleteCategory(3L);
    }

    @Test
    void updateCategoryReturnsOkAndDelegates() {
        CategoryDTO request = new CategoryDTO(null, "Apparel");
        CategoryDTO updated = new CategoryDTO(3L, "Apparel");
        when(categoryService.updateCategory(request, 3L)).thenReturn(updated);

        var response = controller.updateCategory(request, 3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
        verify(categoryService).updateCategory(request, 3L);
    }
}
