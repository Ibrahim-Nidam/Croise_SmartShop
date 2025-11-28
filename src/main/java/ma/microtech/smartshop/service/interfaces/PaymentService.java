package ma.microtech.smartshop.service.interfaces;

import ma.microtech.smartshop.dto.payment.PaymentRequestDTO;
import ma.microtech.smartshop.dto.payment.PaymentResponseDTO;
import ma.microtech.smartshop.enums.PaymentStatus;

import java.time.LocalDate;

public interface PaymentService {
    PaymentResponseDTO addPayment(Long orderId, PaymentRequestDTO dto);
    PaymentResponseDTO updatePaymentStatus(Long paymentId, PaymentStatus newStatus, LocalDate dateEncaissement);
//    List<PaymentResponseDTO> getPaymentsByOrder(Long orderId, String role, Long clientId);
}
