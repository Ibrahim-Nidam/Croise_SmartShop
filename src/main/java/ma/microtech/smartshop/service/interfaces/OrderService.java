package ma.microtech.smartshop.service.interfaces;

import ma.microtech.smartshop.dto.order.OrderCreateRequestDTO;
import ma.microtech.smartshop.dto.order.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderCreateRequestDTO dto);
}
