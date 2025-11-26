package ma.microtech.smartshop.dto.order;

import ma.microtech.smartshop.dto.orderItem.OrderItemResponseDTO;
import ma.microtech.smartshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        Long clientId,
        String clientName,
        LocalDateTime dateCreation,
        BigDecimal sousTotalHT,
        BigDecimal remisePourcentage,
        BigDecimal montantRemise,
        BigDecimal montantTVA,
        BigDecimal totalTTC,
        String codePromo,
        OrderStatus statut,
        List<OrderItemResponseDTO> items
) {
}
