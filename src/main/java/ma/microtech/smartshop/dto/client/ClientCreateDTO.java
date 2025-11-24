package ma.microtech.smartshop.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientCreateDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
