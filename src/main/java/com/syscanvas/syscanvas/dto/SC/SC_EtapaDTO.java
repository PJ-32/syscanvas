package com.syscanvas.syscanvas.dto.SC;

import lombok.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;

/*
 * DTO para Etapa con validaciones Jakarta.
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_EtapaDTO {
    private Long codEtapa;
    
    @NotBlank(message = "El nombre de la etapa es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nomEtapa;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String desEtapa;
    
    @NotNull(message = "El número de etapa es obligatorio")
    @Positive(message = "El número de etapa debe ser positivo")
    private Integer numEtapa;
    
    @NotNull(message = "La vigencia es obligatoria")
    @Min(value = 0, message = "La vigencia debe ser 0 o 1")
    @Max(value = 1, message = "La vigencia debe ser 0 o 1")
    private Integer vigencia;
    
    @NotNull(message = "El canvas es obligatorio")
    @Valid
    private SC_CanvasDTO canvas;
}
