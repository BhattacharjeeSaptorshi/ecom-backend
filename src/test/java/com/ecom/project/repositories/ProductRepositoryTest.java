package com.ecom.project.repositories;

import com.ecom.project.entity.Category;
import com.ecom.project.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findsProductsForCategoryInAscendingPriceOrder() {
        Category category = categoryRepository.save(new Category(null, "Footwear"));
        productRepository.saveAll(List.of(
                product("Premium boots", 120.0, category),
                product("Canvas shoes", 45.0, category),
                product("Travel bag", 80.0, categoryRepository.save(new Category(null, "Accessories")))
        ));

        Page<Product> result = productRepository.findByCategoryOrderByPriceAsc(
                category, PageRequest.of(0, 10, Sort.by("price").ascending()));

        assertEquals(2, result.getTotalElements());
        assertEquals(List.of("Canvas shoes", "Premium boots"),
                result.getContent().stream().map(Product::getProductName).toList());
    }

    @Test
    void searchesProductNameIgnoringCaseWithLikePattern() {
        Category category = categoryRepository.save(new Category(null, "Footwear"));
        productRepository.saveAll(List.of(
                product("Running Sneaker", 90.0, category),
                product("Leather boot", 130.0, category)
        ));

        Page<Product> result = productRepository.findByProductNameLikeIgnoreCase(
                "%SNEAKER%", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Running Sneaker", result.getContent().get(0).getProductName());
    }

    @Test
    void savesAndRetrievesProduct() {
        Category category = categoryRepository.save(new Category(null, "Footwear"));
        Product saved = productRepository.save(product("Canvas shoes", 45.0, category));

        Product retrieved = productRepository.findById(saved.getProductId()).orElseThrow();

        assertEquals(saved.getProductId(), retrieved.getProductId());
        assertEquals("Footwear", retrieved.getCategory().getCategoryName());
        assertTrue(retrieved.getPrice() > 0);
    }

    private static Product product(String name, double price, Category category) {
        Product product = new Product();
        product.setProductName(name);
        product.setDescription("Comfortable everyday footwear");
        product.setQuantity(5);
        product.setPrice(price);
        product.setDiscount(0);
        product.setSpecialPrice(price);
        product.setCategory(category);
        return product;
    }
}
