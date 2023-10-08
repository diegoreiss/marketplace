package com.springboot.marketplace.dto.request;

import jakarta.validation.constraints.Email;

public record RegisterRequestDTO(
        String nickname,

        @Email
        String email,

        String password) {
}
