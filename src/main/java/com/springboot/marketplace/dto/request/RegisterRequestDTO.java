package com.springboot.marketplace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank
        @Size(min = 3, max = 100, message = "Nickname deve estar entre 3 e 100")
        String nickname,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 16, message = "Senha deve estar entre 3 e 16")
        String password
) {
}
