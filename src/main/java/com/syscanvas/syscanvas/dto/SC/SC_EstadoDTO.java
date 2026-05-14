package com.syscanvas.syscanvas.dto.SC;

import lombok.*;
import jakarta.validation.constraints.*;

/*
 * DTO para Estado con validaciones Jakarta.
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_EstadoDTO {
    @NotNull(message = "El código de estado es obligatorio")
    @Positive(message = "El código de estado debe ser positivo")
    private Integer codEstado;
    
    @NotBlank(message = "El nombre del estado es obligatorio")
    @Size(max = 20, message = "El nombre no puede exceder 20 caracteres")
    private String nomEstado;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String desEstado;
    
    @NotNull(message = "La vigencia es obligatoria")
    @Min(value = 0, message = "La vigencia debe ser 0 o 1")
    @Max(value = 1, message = "La vigencia debe ser 0 o 1")
    private Integer vigente;
}
