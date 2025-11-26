package ma.microtech.smartshop.service.impl;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.product.ProductCreateDTO;
import ma.microtech.smartshop.dto.product.ProductResponseDTO;
import ma.microtech.smartshop.dto.product.ProductSearchCriteria;
import ma.microtech.smartshop.dto.product.ProductUpdateDTO;
import ma.microtech.smartshop.entity.Product;
import ma.microtech.smartshop.exception.ForbiddenException;
import ma.microtech.smartshop.exception.NotFoundException;
import ma.microtech.smartshop.mapper.ProductMapper;
import ma.microtech.smartshop.repository.ProductRepository;
import ma.microtech.smartshop.service.interfaces.AuthService;
import ma.microtech.smartshop.service.interfaces.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

        if(product.isDeleted()){
            throw new RuntimeException("Cannot updated deleted Product");
        }

        productMapper.updateEntityFromDTO(dto, product);
        product = productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public Page<Product> getProducts(ProductSearchCriteria criteria, Pageable pageable){
        Specification<Product> spec = ((root, query, criteriaBuilder) ->{

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
            if(criteria.name() != null && !criteria.name().isBlank()){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + criteria.name().toLowerCase() + "%"));
            }

            if(criteria.stock() != null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stock"), criteria.stock()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        return productRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id){
        checkAdmin();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));
        if (product.isDeleted()){
            throw new RuntimeException("Product Already deleted");
        }

        boolean isUsedInOrders = productRepository.isUsedInAnyOrder(id);

        if(isUsedInOrders){
            product.setDeleted(true);
            productRepository.save(product);
        } else {
            productRepository.delete(product);
        }
    }
}
