package ma.microtech.smartshop.service.interfaces;

import ma.microtech.smartshop.dto.product.ProductCreateDTO;
import ma.microtech.smartshop.dto.product.ProductResponseDTO;
import ma.microtech.smartshop.dto.product.ProductSearchCriteria;
import ma.microtech.smartshop.dto.product.ProductUpdateDTO;
import ma.microtech.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDTO createProduct(ProductCreateDTO dto);
    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO dto);
    void deleteProduct(Long id);

    Page<Product> getProducts(ProductSearchCriteria criteria, Pageable pageable);
}
