package ma.microtech.smartshop.dto.product;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal pricePerUnit,
        Integer stock
) {
}
