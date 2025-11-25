package ma.microtech.smartshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.client.ClientCreateDTO;
import ma.microtech.smartshop.dto.client.ClientResponseDTO;
import ma.microtech.smartshop.dto.client.ClientUpdateDTO;
import ma.microtech.smartshop.service.interfaces.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientCreateDTO dto){
        return ResponseEntity.ok(clientService.createClient(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ClientUpdateDTO dto){
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @GetMapping("/me")
    public ResponseEntity<ClientResponseDTO> getMyProfile(){
        return ResponseEntity.ok(clientService.getCurrentClient());
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAll(){
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
