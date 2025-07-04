package com.biopark.cepex_system.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(
        @NotBlank String login,
        @NotBlank @Email String email, // Adicionado email
        @NotBlank String password,
        @NotNull UserRole role
) {
}