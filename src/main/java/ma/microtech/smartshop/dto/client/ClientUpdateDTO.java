package ma.microtech.smartshop.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record ClientUpdateDTO(
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters and spaces")
        String name,
        @Email String email
) {
}
