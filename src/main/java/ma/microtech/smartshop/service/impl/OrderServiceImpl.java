package ma.microtech.smartshop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.config.AppConfig;
import ma.microtech.smartshop.dto.order.*;
import ma.microtech.smartshop.dto.orderItem.OrderItemRequestDTO;
import ma.microtech.smartshop.entity.*;
import ma.microtech.smartshop.enums.CustomerTier;
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
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private final PromoCodeServiceImpl promoCodeService;
    private final AppConfig appConfig;

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

        calculatesTotalAndDiscounts(order, client);

        order = orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        upgradeClientOrderStats(client);
        clientRepository.save(client);

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

    private void calculatesTotalAndDiscounts(Order order, Client client){
        BigDecimal sousTotalHT = order.getSousTotalHT();
        BigDecimal tierDiscount = client.getTier().getDiscountPercent(sousTotalHT);
        BigDecimal promoDiscount = BigDecimal.ZERO;
        String code = order.getCodePromo();

        if (code != null && !code.isBlank()) {
            if (!promoCodeService.isValidFormat(code)) {
                throw new BusinessException("Invalid promo code format. Must be PROMO-XXXX (e.g. PROMO-2025)");
            }
            if (!promoCodeService.isUnused(code)) {
                throw new BusinessException("This promo code has already been used: " + code);
            }
            promoDiscount = promoCodeService.getDiscountPercent(code);
        }

        BigDecimal totalDiscountPercentage = tierDiscount.add(promoDiscount);
        BigDecimal montantRemise = sousTotalHT.multiply(totalDiscountPercentage)
                .divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);

        BigDecimal montantAfterRemise = sousTotalHT.subtract(montantRemise);

        BigDecimal montantTVA = montantAfterRemise.multiply(appConfig.getTvaRate())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalTTC = montantAfterRemise.add(montantTVA);

        order.setRemisePourcentage(totalDiscountPercentage);
        order.setMontantRemise(montantRemise);
        order.setMontantTVA(montantTVA);
        order.setTotalTTC(totalTTC);
        order.setMontantRestant(totalTTC);
        order.setMontantReserve(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus newStatus){
        checkAdmin();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order Not Found"));

        if(order.getStatus() == OrderStatus.REJECTED || order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.CONFIRMED){
            throw new BusinessException("Cannot modify order in final state: " + order.getStatus());
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order is not in PENDING state");
        }

        switch (newStatus){
            case CANCELED -> cancelOrder(order);
            case CONFIRMED -> confirmOrder(order);
            default -> throw new BusinessException("Invalid status transition to: " + newStatus);
        }

        order = orderRepository.save(order);
        return orderMapper.toResponseDTO(order);
    }

    private void confirmOrder(Order order){
        Client client = order.getClient();
        BigDecimal totalSpent = client.getTotalSpent() != null ? client.getTotalSpent() : BigDecimal.ZERO;

        client.setTotalSpent(totalSpent.add(order.getTotalTTC()));
        upgradeClientTier(client);
        clientRepository.save(client);

        order.setStatus(OrderStatus.CONFIRMED);
    }

    private void cancelOrder(Order order){
        for (OrderItem item : order.getItems()){
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        Client client = order.getClient();
        if(client.getTotalOrders() != null && client.getTotalOrders() > 0){
            client.setTotalOrders(client.getTotalOrders() - 1);
        }

        order.setStatus(OrderStatus.CANCELED);
    }

    private void upgradeClientTier(Client client){
        BigDecimal totalSpent = client.getTotalSpent() != null ? client.getTotalSpent() : BigDecimal.ZERO;
        Integer orderCount = client.getTotalOrders() != null ? client.getTotalOrders() : 0;


        CustomerTier newTier = CustomerTier.BASIC;

        if (orderCount >= 20 || totalSpent.compareTo(BigDecimal.valueOf(15000)) >= 0) {
            newTier = CustomerTier.PLATINUM;
        }
        else if (orderCount >= 10 || totalSpent.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            newTier = CustomerTier.GOLD;
        }
        else if (orderCount >= 3 || totalSpent.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            newTier = CustomerTier.SILVER;
        }

        if (client.getTier() != newTier) {
            client.setTier(newTier);
        }
    }

    private void upgradeClientOrderStats(Client client){
        Integer currentorders = client.getTotalOrders() != null ? client.getTotalOrders() : 0;
        client.setTotalOrders(currentorders + 1);

        LocalDate today = LocalDate.now();

        if(client.getFirstOrderDate() == null){
            client.setFirstOrderDate(today);
        }

        client.setLastOrderDate(today);
    }

    @Override
    public OrderResponseDTO getById(Long id){
        if(!authService.hasRole(request, "ADMIN")){
            throw new ForbiddenException("Only Admin can get Order");
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client Not Found"));

        return orderMapper.toResponseDTO(order);
    }
}