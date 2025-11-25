package ma.microtech.smartshop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.client.ClientCreateDTO;
import ma.microtech.smartshop.dto.client.ClientResponseDTO;
import ma.microtech.smartshop.dto.client.ClientUpdateDTO;
import ma.microtech.smartshop.dto.order.OrderSummaryDTO;
import ma.microtech.smartshop.entity.Client;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.enums.UserRole;
import ma.microtech.smartshop.exception.ForbiddenException;
import ma.microtech.smartshop.exception.UnauthorizedException;
import ma.microtech.smartshop.mapper.ClientMapper;
import ma.microtech.smartshop.repository.ClientRepository;
import ma.microtech.smartshop.repository.UserRepository;
import ma.microtech.smartshop.service.interfaces.AuthService;
import ma.microtech.smartshop.service.interfaces.ClientService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final HttpServletRequest request;
    private final ClientMapper clientMapper;

    @Override
    @Transactional
    public ClientResponseDTO createClient(ClientCreateDTO dto){
        if(!authService.hasRole(request, "ADMIN")){
            throw new ForbiddenException("Only Admin can create Clients");
        }

        User user = User.builder()
                .username(dto.email())
                .password(dto.password())
                .role(UserRole.CLIENT)
                .build();

        user = userRepository.save(user);

        Client client = Client.builder()
                .name(dto.name())
                .email(dto.email())
                .user(user)
                .build();

        client = clientRepository.save(client);

        return clientMapper.toResponseDTO(client);
    }

    @Override
    @Transactional
    public ClientResponseDTO updateClient(Long id, ClientUpdateDTO dto){
        User currentUser = authService.getCurrentUser(request);
        if(currentUser == null) throw new UnauthorizedException("Not authenticated");

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client Not Found"));

        if(authService.hasRole(request, "CLIENT")){
                throw new ForbiddenException("Only admin can update profile");
        }

        clientMapper.updateClientFromDTO(dto, client);

        if (dto.email() != null) {
            client.getUser().setUsername(dto.email());
        }

        client = clientRepository.save(client);
        return clientMapper.toResponseDTO(client);
    }

    @Override
    public ClientResponseDTO getCurrentClient(){
        User currentUser = authService.getCurrentUser(request);
        if(currentUser == null) throw new UnauthorizedException("Not Authorized");
        if(authService.hasRole(request, "ADMIN")) throw new ForbiddenException("Use Admin Endpoints");

        Client client=  clientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        return clientMapper.toResponseDTO(client);
    }

    @Override
    public List<ClientResponseDTO> getAllClients(){
        if(!authService.hasRole(request, "ADMIN")){
            throw new ForbiddenException("Only Admin can list Clients");
        }

        return clientMapper.toResponseDTOList(clientRepository.findAll());
    }

    @Override
    public ClientResponseDTO getClientById(Long id){
        if(!authService.hasRole(request, "ADMIN")){
            throw new ForbiddenException("Only Admin can get Client");
        }

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client Not Found"));

        return clientMapper.toResponseDTO(client);
    }

    @Override
    @Transactional
    public void deleteClient(Long id){
        if(!authService.hasRole(request, "ADMIN")){
            throw new ForbiddenException("Only Admin can delete Client");
        }

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client Not Found"));

        if(client.getUser() != null){
            userRepository.delete(client.getUser());
        }

        clientRepository.delete(client);
    }

    @Override
    public List<OrderSummaryDTO> getClientOrderHistory(Long clientId){
        User currentUser = authService.getCurrentUser(request);
        if(currentUser == null){
            throw new UnauthorizedException("Authentication required");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client Not Found"));

        boolean isAdmin = authService.hasRole(request, "ADMIN");
        boolean isOwner = client.getUser() != null &&
                client.getUser().getId().equals(currentUser.getId());

        if(!isAdmin && !isOwner){
            throw new ForbiddenException("You can view only your History");
        }

        return Collections.emptyList();
    }

    @Override
    public List<OrderSummaryDTO> getMyOrderHistory(){
        User currentUser = authService.getCurrentUser(request);
        if(currentUser == null){
            throw new UnauthorizedException("Authentication required");
        }

        if(authService.hasRole(request, "ADMIN")){
            throw new ForbiddenException("Use admin endpoints to view client orders");
        }

        Client client = clientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Your client profile not found"));

        return getClientOrderHistory(client.getId());
    }
}
