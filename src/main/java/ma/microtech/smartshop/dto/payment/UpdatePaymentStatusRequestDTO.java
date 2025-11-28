package ma.microtech.smartshop.dto.payment;

import jakarta.validation.constraints.NotNull;
import ma.microtech.smartshop.enums.PaymentStatus;

import java.time.LocalDate;

public record UpdatePaymentStatusRequestDTO(
        @NotNull(message = "the status is required")
        PaymentStatus status,
        LocalDate dateEncaissement
) {}