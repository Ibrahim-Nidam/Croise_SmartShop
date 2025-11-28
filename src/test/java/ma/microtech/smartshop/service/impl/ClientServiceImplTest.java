package ma.microtech.smartshop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import ma.microtech.smartshop.dto.client.ClientCreateDTO;
import ma.microtech.smartshop.dto.client.ClientResponseDTO;
import ma.microtech.smartshop.dto.client.ClientUpdateDTO;
import ma.microtech.smartshop.entity.Client;
import ma.microtech.smartshop.entity.User;
import ma.microtech.smartshop.enums.UserRole;
import ma.microtech.smartshop.exception.ForbiddenException;
import ma.microtech.smartshop.mapper.ClientMapper;
import ma.microtech.smartshop.repository.ClientRepository;
import ma.microtech.smartshop.repository.UserRepository;
import ma.microtech.smartshop.service.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private User mockUser;
    private Client mockClient;
    private ClientCreateDTO createDTO;
    private ClientUpdateDTO updateDTO;
    private ClientResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("test@example.com")
                .password("plainPassword123")
                .role(UserRole.ADMIN)
                .build();

        mockClient = Client.builder()
                .id(1L)
                .name("Test Client")
                .email("test@example.com")
                .user(mockUser)
                .build();

        createDTO = new ClientCreateDTO("Test Client", "test@example.com", "password123");
        updateDTO = new ClientUpdateDTO("Updated Client", "updated@example.com");
        responseDTO = new ClientResponseDTO(
                1L,
                "Test Client",
                "test@example.com",
                null,
                0,
                null,
                null,
                null
        );
    }

    @Test
    void createClient_Success() {
        when(authService.hasRole(request, "ADMIN")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(clientRepository.save(any(Client.class))).thenReturn(mockClient);
        when(clientMapper.toResponseDTO(any(Client.class))).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.createClient(createDTO);

        assertNotNull(result);
        assertEquals("Test Client", result.name());
        assertEquals("test@example.com", result.email());
        verify(authService).hasRole(request, "ADMIN");
        verify(userRepository).save(any(User.class));
        verify(clientRepository).save(any(Client.class));
        verify(clientMapper).toResponseDTO(any(Client.class));
    }

    @Test
    void updateClient_Success() {
        when(authService.getCurrentUser(request)).thenReturn(mockUser);
        when(authService.hasRole(request, "CLIENT")).thenReturn(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(mockClient));
        when(clientRepository.save(any(Client.class))).thenReturn(mockClient);
        when(clientMapper.toResponseDTO(any(Client.class))).thenReturn(responseDTO);

        ClientResponseDTO result = clientService.updateClient(1L, updateDTO);

        assertNotNull(result);
        verify(authService).getCurrentUser(request);
        verify(clientRepository).findById(1L);
        verify(clientMapper).updateClientFromDTO(updateDTO, mockClient);
        verify(clientRepository).save(mockClient);
        verify(clientMapper).toResponseDTO(mockClient);
    }

    @Test
    void getClientById_ThrowForbiddenException() {
        Long clientId = 1L;
        when(authService.hasRole(request, "ADMIN")).thenReturn(false);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> clientService.getClientById(clientId)
        );

        assertEquals("Only Admin can get Client", exception.getMessage());
        verify(authService).hasRole(request, "ADMIN");
        verify(clientRepository, never()).findById(anyLong());
    }
}