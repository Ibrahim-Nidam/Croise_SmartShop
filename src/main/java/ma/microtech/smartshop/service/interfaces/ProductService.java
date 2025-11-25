package ma.microtech.smartshop.service.interfaces;

import ma.microtech.smartshop.dto.product.ProductCreateDTO;
import ma.microtech.smartshop.dto.product.ProductResponseDTO;
import ma.microtech.smartshop.dto.product.ProductUpdateDTO;

public interface ProductService {
    ProductResponseDTO createProduct(ProductCreateDTO dto);
    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO dto);
}
