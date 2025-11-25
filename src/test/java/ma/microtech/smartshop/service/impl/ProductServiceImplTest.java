package ma.microtech.smartshop.service.impl;

import ma.microtech.smartshop.dto.product.ProductSearchCriteria;
import ma.microtech.smartshop.entity.Product;
import ma.microtech.smartshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private List<Product> mockProducts;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setStock(10);
        product1.setDeleted(false);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setStock(50);
        product2.setDeleted(false);

        mockProducts = Arrays.asList(product1, product2);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getProducts_WithNoFilters() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, null);
        Page<Product> expectedPage = new PageImpl<>(mockProducts, pageable, mockProducts.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Product> result = productService.getProducts(criteria, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_WithNameFilter() {
        ProductSearchCriteria criteria = new ProductSearchCriteria("laptop", null);
        Product laptop = mockProducts.get(0);
        Page<Product> expectedPage = new PageImpl<>(List.of(laptop), pageable, 1);

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Product> result = productService.getProducts(criteria, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_WithStockFilter() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, 20);
        Product mouse = mockProducts.get(1);
        Page<Product> expectedPage = new PageImpl<>(List.of(mouse), pageable, 1);

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Product> result = productService.getProducts(criteria, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).getStock() >= 20);
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}