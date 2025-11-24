package ma.microtech.smartshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.microtech.smartshop.dto.client.ClientCreateDTO;
import ma.microtech.smartshop.dto.client.ClientResponseDTO;
import ma.microtech.smartshop.dto.client.ClientUpdateDTO;
import ma.microtech.smartshop.service.interfaces.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
