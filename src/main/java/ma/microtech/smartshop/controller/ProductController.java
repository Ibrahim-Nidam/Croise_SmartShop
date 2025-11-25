package ma.microtech.smartshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.product.ProductCreateDTO;
import ma.microtech.smartshop.dto.product.ProductResponseDTO;
import ma.microtech.smartshop.dto.product.ProductUpdateDTO;
import ma.microtech.smartshop.service.interfaces.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateDTO dto){
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto){
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }
}
