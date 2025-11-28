package ma.microtech.smartshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.payment.PaymentRequestDTO;
import ma.microtech.smartshop.dto.payment.PaymentResponseDTO;
import ma.microtech.smartshop.dto.payment.UpdatePaymentStatusRequestDTO;
import ma.microtech.smartshop.service.interfaces.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponseDTO> updateStatus(@PathVariable Long paymentId, @Valid @RequestBody UpdatePaymentStatusRequestDTO dto){
        PaymentResponseDTO responseDTO = paymentService.updatePaymentStatus(paymentId, dto.status(), dto.dateEncaissement());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/orders/{orderId}/payments")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsForOrder(@PathVariable Long orderId, HttpServletRequest request){
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByOrderId(orderId, request);
        return ResponseEntity.ok(payments);
    }
}




