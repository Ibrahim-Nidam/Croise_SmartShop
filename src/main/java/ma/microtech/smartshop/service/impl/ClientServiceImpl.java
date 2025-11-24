package ma.microtech.smartshop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.client.ClientCreateDTO;
import ma.microtech.smartshop.dto.client.ClientResponseDTO;
import ma.microtech.smartshop.dto.client.ClientUpdateDTO;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder;
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
                .password(passwordEncoder.encode(dto.password()))
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

}
