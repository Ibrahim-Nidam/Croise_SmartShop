package ma.microtech.smartshop.mapper;

import ma.microtech.smartshop.dto.orderItem.OrderItemResponseDTO;
import ma.microtech.smartshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponseDTO toResponseDTO(OrderItem item);

    List<OrderItemResponseDTO> toResponseDTOList(List<OrderItem> items);
}
