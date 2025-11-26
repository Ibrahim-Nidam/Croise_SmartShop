package ma.microtech.smartshop.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ma.microtech.smartshop.dto.orderItem.OrderItemRequestDTO;

import java.util.List;

public record OrderCreateRequestDTO(
        @NotNull Long clientId,
        List<@Valid OrderItemRequestDTO> items,
        String codePromo
) {
}
