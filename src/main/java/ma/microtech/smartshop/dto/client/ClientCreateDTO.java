package ma.microtech.smartshop.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientCreateDTO(
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters and spaces")
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
