package ma.microtech.smartshop.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import ma.microtech.smartshop.dto.payment.PaymentRequestDTO;
import ma.microtech.smartshop.dto.payment.PaymentResponseDTO;
import ma.microtech.smartshop.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {
    PaymentResponseDTO addPayment(Long orderId, PaymentRequestDTO dto);
    PaymentResponseDTO updatePaymentStatus(Long paymentId, PaymentStatus newStatus, LocalDate dateEncaissement);
    List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId, HttpServletRequest request);
}
