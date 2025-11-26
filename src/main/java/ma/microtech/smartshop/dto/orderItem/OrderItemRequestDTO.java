package ma.microtech.smartshop.dto.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(
        @NotNull Long productId,
        @Min(1) Integer quantity
) {
}
