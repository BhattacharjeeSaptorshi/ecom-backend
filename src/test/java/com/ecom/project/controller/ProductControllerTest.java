package com.ecom.project.controller;

import com.ecom.project.payload.ProductDTO;
import com.ecom.project.payload.ProductResponse;
import com.ecom.project.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    private ProductService productService;
    private ProductController controller;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        controller = new ProductController(productService);
    }

    @Test
    void addProductReturnsCreatedAndDelegates() {
        ProductDTO request = new ProductDTO();
        ProductDTO saved = new ProductDTO();
        when(productService.addProduct(7L, request)).thenReturn(saved);

        var response = controller.addProduct(request, 7L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
        verify(productService).addProduct(7L, request);
    }

    @Test
    void getAllProductsReturnsOkAndDelegatesPagination() {
        ProductResponse products = new ProductResponse();
        when(productService.getAllProducts(1, 10, "price", "desc")).thenReturn(products);

        var response = controller.getAllProducts(1, 10, "price", "desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(products, response.getBody());
        verify(productService).getAllProducts(1, 10, "price", "desc");
    }

    @Test
    void getProductsByCategoryReturnsOkAndDelegates() {
        ProductResponse products = new ProductResponse();
        when(productService.searchByCategory(3L, 0, 5, "price", "asc")).thenReturn(products);

        var response = controller.getProductsByCategory(3L, 0, 5, "price", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(products, response.getBody());
        verify(productService).searchByCategory(3L, 0, 5, "price", "asc");
    }

    @Test
    void getProductsByKeywordReturnsFoundAndDelegates() {
        ProductResponse products = new ProductResponse();
        when(productService.searchProductByKeyword("shoe", 0, 5, "price", "asc")).thenReturn(products);

        var response = controller.getProductsByKeyword("shoe", 0, 5, "price", "asc");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertSame(products, response.getBody());
        verify(productService).searchProductByKeyword("shoe", 0, 5, "price", "asc");
    }

    @Test
    void updateProductReturnsOkAndDelegates() {
        ProductDTO request = new ProductDTO();
        ProductDTO updated = new ProductDTO();
        when(productService.updateProduct(8L, request)).thenReturn(updated);

        var response = controller.updateProduct(request, 8L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
        verify(productService).updateProduct(8L, request);
    }

    @Test
    void deleteProductReturnsOkAndDelegates() {
        ProductDTO deleted = new ProductDTO();
        when(productService.deleteProduct(9L)).thenReturn(deleted);

        var response = controller.deleteProduct(9L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(deleted, response.getBody());
        verify(productService).deleteProduct(9L);
    }

    @Test
    void updateProductImageReturnsOkAndDelegates() throws IOException {
        MockMultipartFile image = new MockMultipartFile("image", "shoe.png", "image/png", new byte[]{1, 2});
        ProductDTO updated = new ProductDTO();
        when(productService.updateProductImage(9L, image)).thenReturn(updated);

        var response = controller.updateProductImage(9L, image);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updated, response.getBody());
        verify(productService).updateProductImage(9L, image);
    }
}
