package com.ecom.project.repositories;

import com.ecom.project.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findsCategoryByName() {
        categoryRepository.save(new Category(null, "Footwear"));

        Category result = categoryRepository.findByCategoryName("Footwear");

        assertNotNull(result);
        assertEquals("Footwear", result.getCategoryName());
    }

    @Test
    void persistsCategoryAndSupportsJpaCrudOperations() {
        Category saved = categoryRepository.save(new Category(null, "Accessories"));

        Category found = categoryRepository.findById(saved.getCategoryId()).orElseThrow();
        assertEquals("Accessories", found.getCategoryName());
        assertEquals(1, categoryRepository.count());

        categoryRepository.delete(found);
        assertTrue(categoryRepository.findById(saved.getCategoryId()).isEmpty());
    }
}
