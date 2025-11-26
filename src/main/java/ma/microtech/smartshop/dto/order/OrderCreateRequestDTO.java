package ma.microtech.smartshop.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ma.microtech.smartshop.dto.orderItem.OrderItemRequestDTO;

import java.util.List;

public record OrderCreateRequestDTO(
        @NotNull Long clientId,
        @NotEmpty(message = "You should pick at least one Product")
        List<@Valid OrderItemRequestDTO> items,
        @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Code promo must match PROMO-XXXX format")
        String codePromo
) {
}
