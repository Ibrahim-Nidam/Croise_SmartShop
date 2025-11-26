package ma.microtech.smartshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.order.OrderCreateRequestDTO;
import ma.microtech.smartshop.dto.order.OrderResponseDTO;
import ma.microtech.smartshop.service.interfaces.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderCreateRequestDTO dto){
        return ResponseEntity.ok(orderService.createOrder(dto));
    }
}
