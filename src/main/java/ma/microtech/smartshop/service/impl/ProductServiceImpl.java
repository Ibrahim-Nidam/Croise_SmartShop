package ma.microtech.smartshop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.product.ProductCreateDTO;
import ma.microtech.smartshop.dto.product.ProductResponseDTO;
import ma.microtech.smartshop.dto.product.ProductUpdateDTO;
import ma.microtech.smartshop.entity.Product;
import ma.microtech.smartshop.exception.ForbiddenException;
import ma.microtech.smartshop.mapper.ProductMapper;
import ma.microtech.smartshop.repository.ProductRepository;
import ma.microtech.smartshop.service.interfaces.AuthService;
import ma.microtech.smartshop.service.interfaces.ProductService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final HttpServletRequest request;

    private void checkAdmin(){
        if(!authService.hasRole(request, "ADMIN")){
            throw new ForbiddenException("Only Admin can manage Products");
        }
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO dto){
        checkAdmin();
        Product product = productMapper.toEntity(dto);
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO dto){
        checkAdmin();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        if(product.isDeleted()){
            throw new RuntimeException("Cannot updated deleted Product");
        }

        productMapper.updateEntityFromDTO(dto, product);
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }
}
