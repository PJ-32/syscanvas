package com.syscanvas.syscanvas.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.*;
import com.fasterxml.jackson.annotation.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CambiarPasswordRequest {
    @NotNull(message = "El código de persona es obligatorio")
    private Long codPersona;

    private String passwordActual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!()_\\-0-9])(?=\\S+$).{8,}$",
        message = "La contraseña debe contener al menos una mayúscula, una minúscula y un carácter especial o número"
    )
    private String passwordNueva;

    // DNI del usuario (validación de identidad)
    private String dni;

    // Fecha de nacimiento del usuario (validación de identidad)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
}
