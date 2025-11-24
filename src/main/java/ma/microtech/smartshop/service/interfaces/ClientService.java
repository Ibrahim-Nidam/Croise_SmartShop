package ma.microtech.smartshop.service.interfaces;

import ma.microtech.smartshop.dto.client.ClientCreateDTO;
import ma.microtech.smartshop.dto.client.ClientResponseDTO;
import ma.microtech.smartshop.dto.client.ClientUpdateDTO;
import ma.microtech.smartshop.entity.Client;

public interface ClientService {
    ClientResponseDTO createClient(ClientCreateDTO dto);
    ClientResponseDTO updateClient(Long id, ClientUpdateDTO dto);
    ClientResponseDTO getCurrentClient();
}
