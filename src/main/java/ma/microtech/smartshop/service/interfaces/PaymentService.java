package ma.microtech.smartshop.service.interfaces;

import ma.microtech.smartshop.dto.payment.PaymentRequestDTO;
import ma.microtech.smartshop.dto.payment.PaymentResponseDTO;
import ma.microtech.smartshop.entity.Paiement;
import ma.microtech.smartshop.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    PaymentResponseDTO addPayment(Long orderId, PaymentRequestDTO dto);
//    Paiement updatePaymentStatus(Long orderId, PaymentStatus newStatus, LocalDate dateEncaissement);
//    List<PaymentResponseDTO> getPaymentsByOrder(Long orderId, String role, Long clientId);
}
