package ma.microtech.smartshop.mapper;

import ma.microtech.smartshop.dto.order.OrderResponseDTO;
import ma.microtech.smartshop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    @Mapping(target = "statut", source = "status")
    @Mapping(target = "items", source = "items")
    OrderResponseDTO toResponseDTO(Order order);
}
