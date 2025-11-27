package ma.microtech.smartshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.payment.PaymentRequestDTO;
import ma.microtech.smartshop.dto.payment.PaymentResponseDTO;
import ma.microtech.smartshop.service.interfaces.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PaymentResponseDTO> addPayment(@PathVariable Long orderId, @Valid @RequestBody PaymentRequestDTO dto){
        PaymentResponseDTO responseDTO = paymentService.addPayment(orderId, dto);
        return ResponseEntity.ok(responseDTO);
    }
}




