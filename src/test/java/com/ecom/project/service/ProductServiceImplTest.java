package com.ecom.project.service;

import com.ecom.project.entity.Category;
import com.ecom.project.entity.Product;
import com.ecom.project.exceptions.APIException;
import com.ecom.project.exceptions.ResourceNotFoundException;
import com.ecom.project.payload.ProductDTO;
import com.ecom.project.repositories.CategoryRepository;
import com.ecom.project.repositories.ProductRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private FileService fileService;
    @InjectMocks
    private ProductServiceImpl productService;

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        ReflectionTestUtils.setField(productService, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(productService, "path", "test-images");
    }

    @Test
    void addProductCalculatesSpecialPriceAndSetsDefaultImage() {
        Category category = category(1L, "Footwear");
        category.setProducts(new ArrayList<>());
        Product saved = product(2L, "Sneakers", 100.0);
        saved.setDiscount(20.0);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setProductId(2L);
            return product;
        });

        ProductDTO result = productService.addProduct(1L, productDTO("Sneakers", 100.0, 20.0));

        assertEquals(2L, result.getProductId());
        assertEquals("default.png", result.getImage());
        assertEquals(80.0, result.getSpecialPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProductRejectsDuplicateName() {
        Category category = category(1L, "Footwear");
        category.setProducts(List.of(product(2L, "Sneakers", 100.0)));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(APIException.class,
                () -> productService.addProduct(1L, productDTO("Sneakers", 100.0, 0.0)));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void addProductRequiresExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.addProduct(1L, productDTO("Sneakers", 100.0, 0.0)));
    }

    @Test
    void getAllProductsMapsPageAndRejectsEmptyPage() {
        Product firstProduct = product(1L, "Sneakers", 100.0);
        Page<Product> productPage = new PageImpl<>(List.of(firstProduct),
                PageRequest.of(0, 5, Sort.by("price").ascending()), 1);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        var result = productService.getAllProducts(0, 5, "price", "asc");

        assertEquals(1, result.getContent().size());
        assertEquals("Sneakers", result.getContent().get(0).getProductName());
        assertEquals(1, result.getTotalElements());
        when(productRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 5), 0));
        assertThrows(APIException.class, () -> productService.getAllProducts(0, 5, "price", "asc"));
    }

    @Test
    void searchByCategoryUsesCategoryAndMapsResults() {
        Category category = category(1L, "Footwear");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryOrderByPriceAsc(any(Category.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(product(2L, "Sneakers", 100.0))));

        var result = productService.searchByCategory(1L, 0, 5, "price", "asc");

        assertEquals("Sneakers", result.getContent().get(0).getProductName());
        verify(productRepository).findByCategoryOrderByPriceAsc(any(Category.class), any(PageRequest.class));
    }

    @Test
    void searchProductByKeywordPassesWildcardAndRejectsNoMatches() {
        when(productRepository.findByProductNameLikeIgnoreCase(any(String.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(product(2L, "Sneakers", 100.0))));

        var result = productService.searchProductByKeyword("shoe", 0, 5, "price", "desc");

        assertEquals("Sneakers", result.getContent().get(0).getProductName());
        verify(productRepository).findByProductNameLikeIgnoreCase("%shoe%",
                PageRequest.of(0, 5, Sort.by("price").descending()));
        when(productRepository.findByProductNameLikeIgnoreCase(any(String.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
        assertThrows(APIException.class,
                () -> productService.searchProductByKeyword("absent", 0, 5, "price", "asc"));
    }

    @Test
    void updateProductUpdatesFieldsAndRequiresExistingProduct() {
        Product existing = product(2L, "Old name", 20.0);
        when(productRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductDTO updated = productDTO("New name", 50.0, 10.0);
        updated.setDescription("Updated product description");
        updated.setQuantity(4);
        var result = productService.updateProduct(2L, updated);

        assertEquals("New name", result.getProductName());
        assertEquals(50.0, result.getPrice());
        assertEquals(4, result.getQuantity());
        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, updated));
    }

    @Test
    void deleteProductDeletesAndReturnsProductDto() {
        Product product = product(2L, "Sneakers", 100.0);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        var result = productService.deleteProduct(2L);

        assertEquals("Sneakers", result.getProductName());
        verify(productRepository).delete(product);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
    }

    @Test
    void updateProductImageUploadsAndSavesFileName() throws Exception {
        Product product = product(2L, "Sneakers", 100.0);
        MockMultipartFile image = new MockMultipartFile("image", "new.png", "image/png", new byte[]{1});
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(fileService.uploadImage("test-images", image)).thenReturn("stored.png");
        when(productRepository.save(product)).thenReturn(product);

        var result = productService.updateProductImage(2L, image);

        assertEquals("stored.png", result.getImage());
        verify(fileService).uploadImage("test-images", image);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProductImage(99L, image));
    }

    private static ProductDTO productDTO(String name, double price, double discount) {
        ProductDTO dto = new ProductDTO();
        dto.setProductName(name);
        dto.setDescription("Comfortable everyday footwear");
        dto.setQuantity(3);
        dto.setPrice(price);
        dto.setDiscount(discount);
        return dto;
    }

    private static Product product(Long id, String name, double price) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setDescription("Comfortable everyday footwear");
        product.setPrice(price);
        product.setQuantity(3);
        return product;
    }

    private static Category category(Long id, String name) {
        Category category = new Category(id, name);
        return category;
    }
}
