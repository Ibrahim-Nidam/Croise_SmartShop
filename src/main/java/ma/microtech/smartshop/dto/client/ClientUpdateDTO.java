package ma.microtech.smartshop.dto.client;

import jakarta.validation.constraints.Email;

public record ClientUpdateDTO(
        String name,
        @Email String email
) {
}
