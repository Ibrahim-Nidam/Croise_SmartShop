package ma.microtech.smartshop.service.interfaces;

import ma.microtech.smartshop.dto.order.OrderCreateRequestDTO;
import ma.microtech.smartshop.dto.order.OrderResponseDTO;
import ma.microtech.smartshop.enums.OrderStatus;

public interface OrderService {
    OrderResponseDTO createOrder(OrderCreateRequestDTO dto);
    OrderResponseDTO updateOrderStatus(Long id, OrderStatus status);
}
