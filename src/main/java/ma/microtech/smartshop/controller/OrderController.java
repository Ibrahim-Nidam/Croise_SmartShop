package ma.microtech.smartshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.order.OrderCreateRequestDTO;
import ma.microtech.smartshop.dto.order.OrderResponseDTO;
import ma.microtech.smartshop.enums.OrderStatus;
import ma.microtech.smartshop.exception.BusinessException;
import ma.microtech.smartshop.service.interfaces.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderCreateRequestDTO dto){
        return ResponseEntity.ok(orderService.createOrder(dto));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.getById(orderId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body){
        String statusStr = body.get("status");
        if (statusStr == null) {
            throw new BusinessException("Field status is required");
        }
        OrderStatus newStatus;
        try{
            newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException ex){
            throw new BusinessException("Invalid Status : " + statusStr);
        }

        OrderResponseDTO updated = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }
}
