package ma.microtech.smartshop.dto.order;

import ma.microtech.smartshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryDTO(
        Long id,
        LocalDateTime dateCreation,
        BigDecimal totalTTC,
        OrderStatus status
) {
}
