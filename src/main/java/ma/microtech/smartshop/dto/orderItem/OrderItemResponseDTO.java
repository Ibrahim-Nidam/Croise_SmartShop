package ma.microtech.smartshop.dto.orderItem;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal pricePerUnitHT,
        BigDecimal totalLigneHT
) {
}
