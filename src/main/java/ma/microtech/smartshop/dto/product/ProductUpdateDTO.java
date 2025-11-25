package ma.microtech.smartshop.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductUpdateDTO(
        String name,
        @PositiveOrZero BigDecimal pricePerUnit,
        @Min(0) Integer stock
) {
}
