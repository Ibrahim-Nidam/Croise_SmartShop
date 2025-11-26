package ma.microtech.smartshop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.order.*;
import ma.microtech.smartshop.dto.orderItem.OrderItemRequestDTO;
import ma.microtech.smartshop.entity.*;
import ma.microtech.smartshop.enums.OrderStatus;
import ma.microtech.smartshop.exception.BusinessException;
import ma.microtech.smartshop.exception.ForbiddenException;
import ma.microtech.smartshop.exception.NotFoundException;
import ma.microtech.smartshop.mapper.OrderMapper;
import ma.microtech.smartshop.repository.*;
import ma.microtech.smartshop.service.interfaces.AuthService;
import ma.microtech.smartshop.service.interfaces.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;
    private final OrderMapper orderMapper;
    private final HttpServletRequest request;

    private void checkAdmin() {
        if (!authService.hasRole(request, "ADMIN")) {
            throw new ForbiddenException("Only ADMIN can create orders");
        }
    }

    @Override
    public OrderResponseDTO createOrder(OrderCreateRequestDTO dto) {
        checkAdmin();

        Client client = getClientOrThrow(dto.clientId());
        List<String> stockErrors = validateStock(dto.items());

        if (!stockErrors.isEmpty()) {
            return createRejectedOrder(client, dto.codePromo(), stockErrors);
        }

        return createSuccessfulOrder(client, dto);
    }

    private Client getClientOrThrow(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + clientId));
    }

    private List<String> validateStock(List<OrderItemRequestDTO> items) {
        List<String> errors = new ArrayList<>();

        for (OrderItemRequestDTO itemDto : items) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + itemDto.productId()));

            if (product.isDeleted()) {
                errors.add("Product is deleted: " + product.getName());
                continue;
            }

            if (product.getStock() < itemDto.quantity()) {
                errors.add(String.format(
                        "Insufficient stock for '%s' (available: %d, requested: %d)",
                        product.getName(), product.getStock(), itemDto.quantity()
                ));
            }
        }

        return errors;
    }

    private OrderResponseDTO createRejectedOrder(Client client, String codePromo, List<String> errors) {
        Order rejectedOrder = Order.builder()
                .client(client)
                .codePromo(codePromo)
                .status(OrderStatus.REJECTED)
                .sousTotalHT(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        rejectedOrder = orderRepository.save(rejectedOrder);

        throw new BusinessException(
                "Order rejected: " + String.join("; ", errors)
        );
    }

    private OrderResponseDTO createSuccessfulOrder(Client client, OrderCreateRequestDTO dto) {
        Order order = Order.builder()
                .client(client)
                .codePromo(dto.codePromo())
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        BigDecimal sousTotalHT = processOrderItems(order, dto.items());

        order.setSousTotalHT(sousTotalHT);
        order = orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return orderMapper.toResponseDTO(order);
    }

    private BigDecimal processOrderItems(Order order, List<OrderItemRequestDTO> itemDtos) {
        BigDecimal sousTotalHT = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDto : itemDtos) {
            Product product = productRepository.findById(itemDto.productId()).get();

            deductStock(product, itemDto.quantity());

            OrderItem item = createOrderItem(order, product, itemDto.quantity());
            order.getItems().add(item);

            sousTotalHT = sousTotalHT.add(item.getTotalLigneHT());
        }

        return sousTotalHT;
    }

    private void deductStock(Product product, Integer quantity) {
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    private OrderItem createOrderItem(Order order, Product product, Integer quantity) {
        BigDecimal totalLigne = product.getPricePerUnit()
                .multiply(BigDecimal.valueOf(quantity));

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .pricePerUnitHT(product.getPricePerUnit())
                .totalLigneHT(totalLigne)
                .build();
    }
}