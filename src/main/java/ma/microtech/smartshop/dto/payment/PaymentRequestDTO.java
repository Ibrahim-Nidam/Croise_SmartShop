package ma.microtech.smartshop.dto.payment;

import ma.microtech.smartshop.enums.PaymentType;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        BigDecimal montant,
        PaymentType type
) {
}
