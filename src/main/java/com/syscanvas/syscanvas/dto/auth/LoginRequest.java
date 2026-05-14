package com.syscanvas.syscanvas.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/*
 * Request para login con validaciones Jakarta.
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequest {
    @NotNull(message = "El código de persona es obligatorio")
    @Positive(message = "El código de persona debe ser positivo")
    private Long codPersona;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
