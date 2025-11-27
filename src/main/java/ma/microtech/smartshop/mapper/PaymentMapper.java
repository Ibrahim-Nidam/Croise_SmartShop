package ma.microtech.smartshop.mapper;

import ma.microtech.smartshop.dto.payment.PaymentResponseDTO;
import ma.microtech.smartshop.entity.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface PaymentMapper {
    @Mapping(target = "montantRestantApresPaiement", source = "order.montantRestant")
    PaymentResponseDTO toResponseDTO(Paiement paiement);

    List<PaymentResponseDTO> toResponseDTOList(List<Paiement> paiements);
}
