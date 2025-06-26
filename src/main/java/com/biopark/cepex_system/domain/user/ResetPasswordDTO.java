package com.biopark.cepex_system.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(
    @NotBlank(message = "O token é obrigatório")
    String token,
    
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String password,
    
    @NotBlank(message = "A confirmação da senha é obrigatória")
    String confirmPassword
) {} 