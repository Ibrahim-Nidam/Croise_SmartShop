package ma.microtech.smartshop.dto.payment;

import ma.microtech.smartshop.enums.PaymentStatus;
import ma.microtech.smartshop.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long id,
        String numeroPaiement,
        BigDecimal montant,
        PaymentType type,
        PaymentStatus status,
        LocalDateTime datePaiement,
        LocalDate dateEncaissement,
        BigDecimal montantRestantApresPaiement
) {
}
