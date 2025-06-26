package com.biopark.cepex_system.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email
) {} 