package ma.microtech.smartshop.dto.client;

import ma.microtech.smartshop.enums.CustomerTier;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClientResponseDTO(
        Long id,
        String name,
        String email,
        CustomerTier tier,
        Integer totalOrders,
        BigDecimal totalSpent,
        LocalDate firstOrderDate,
        LocalDate lastOrderDate
) {
}
