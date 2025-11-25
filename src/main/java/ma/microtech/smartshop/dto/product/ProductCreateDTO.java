package ma.microtech.smartshop.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductCreateDTO(
        @NotBlank String name,
        @NotNull @PositiveOrZero BigDecimal pricePerUnit,
        @NotNull @Min(0) Integer stock
        ) {
}
