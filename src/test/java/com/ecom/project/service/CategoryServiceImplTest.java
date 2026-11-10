package com.ecom.project.service;

import com.ecom.project.entity.Category;
import com.ecom.project.exceptions.APIException;
import com.ecom.project.exceptions.ResourceNotFoundException;
import com.ecom.project.payload.CategoryDTO;
import com.ecom.project.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(categoryService, "modelMapper", new ModelMapper());
    }

    @Test
    void getAllCategoriesMapsPageAndRejectsEmptyRepository() {
        Category category = category(1L, "Footwear");
        Page<Category> page = new PageImpl<>(List.of(category),
                PageRequest.of(0, 5, Sort.by("categoryName").ascending()), 1);
        when(categoryRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(categoryRepository.count()).thenReturn(1L);

        var result = categoryService.getAllCategories(0, 5, "categoryName", "asc");

        assertEquals("Footwear", result.getContent().get(0).getCategoryName());
        assertEquals(1, result.getTotalElements());
        when(categoryRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(categoryRepository.count()).thenReturn(0L);
        assertThrows(APIException.class,
                () -> categoryService.getAllCategories(0, 5, "categoryName", "asc"));
    }

    @Test
    void getCategoryReturnsDtoAndThrowsWhenMissing() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category(1L, "Footwear")));

        assertEquals("Footwear", categoryService.getCategory(1L).getCategoryName());
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategory(99L));
    }

    @Test
    void createCategorySavesUniqueCategoryAndRejectsDuplicate() {
        CategoryDTO request = new CategoryDTO(null, "Footwear");
        when(categoryRepository.findByCategoryName("Footwear")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setCategoryId(4L);
            return category;
        });

        var result = categoryService.createCategory(request);

        assertEquals(4L, result.getCategoryId());
        assertEquals("Footwear", result.getCategoryName());
        when(categoryRepository.findByCategoryName("Footwear")).thenReturn(category(1L, "Footwear"));
        assertThrows(APIException.class, () -> categoryService.createCategory(request));
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deleteCategoryDeletesAndReturnsDto() {
        Category category = category(1L, "Footwear");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        var result = categoryService.deleteCategory(1L);

        assertEquals("Footwear", result.getCategoryName());
        verify(categoryRepository).delete(category);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(99L));
    }

    @Test
    void updateCategoryChangesNameAndRequiresExistingCategory() {
        Category existing = category(1L, "Footwear");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        var result = categoryService.updateCategory(new CategoryDTO(null, "Apparel"), 1L);

        assertEquals("Apparel", result.getCategoryName());
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(new CategoryDTO(null, "Apparel"), 99L));
    }

    private static Category category(Long id, String name) {
        return new Category(id, name);
    }
}
