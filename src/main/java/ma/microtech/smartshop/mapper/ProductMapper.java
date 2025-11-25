package ma.microtech.smartshop.mapper;

import ma.microtech.smartshop.dto.product.ProductCreateDTO;
import ma.microtech.smartshop.dto.product.ProductResponseDTO;
import ma.microtech.smartshop.dto.product.ProductUpdateDTO;
import ma.microtech.smartshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDTO toResponseDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Product toEntity(ProductCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDTO(ProductUpdateDTO dto, @MappingTarget Product product);
}
