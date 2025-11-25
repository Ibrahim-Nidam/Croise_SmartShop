package ma.microtech.smartshop.mapper;

import ma.microtech.smartshop.dto.client.ClientResponseDTO;
import ma.microtech.smartshop.dto.client.ClientUpdateDTO;
import ma.microtech.smartshop.entity.Client;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(target = "tier", source = "tier")
    @Mapping(target= "totalOrders", source = "totalOrders")
    @Mapping(target = "totalSpent", source = "totalSpent")
    @Mapping(target = "firstOrderDate", source = "firstOrderDate")
    @Mapping(target = "lastOrderDate", source = "lastOrderDate")
    ClientResponseDTO toResponseDTO(Client client);

    List<ClientResponseDTO> toResponseDTOList(List<Client> clients);

    @InheritInverseConfiguration(name = "toResponseDTO")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "firstOrderDate", ignore = true)
    @Mapping(target = "lastOrderDate", ignore = true)
    void updateClientFromDTO(ClientUpdateDTO dto, @MappingTarget Client client);
}
