package com.syscanvas.syscanvas.dto.T;

import lombok.*;
import jakarta.validation.constraints.*;

/*
 * DTO para Proyecto con validaciones Jakarta.
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class T_ProyectoDTO {
    /*
     * Código del proyecto.
     */
    @NotNull(message = "El código del proyecto es obligatorio")
    @Positive(message = "El código del proyecto debe ser positivo")
    private Long codPyto;
    
    /*
     * Nombre del proyecto.
     */
    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(min = 5, max = 1000, message = "El nombre debe tener entre 5 y 1000 caracteres")
    private String nomPyto;
    
    /*
     * Vigencia del proyecto (1 = activo, 0 = inactivo).
     */
    @Min(value = 0, message = "La vigencia debe ser 0 o 1")
    @Max(value = 1, message = "La vigencia debe ser 0 o 1")
    private Integer vigente;
    
    /*
     * Año de inicio del proyecto.
     */
    @Min(value = 2000, message = "Año inicial debe ser mayor o igual a 2000")
    @Max(value = 2100, message = "Año inicial debe ser menor o igual a 2100")
    private Integer annoIni;
    
    /*
     * Año de finalización del proyecto.
     */
    @Min(value = 2000, message = "Año final debe ser mayor o igual a 2000")
    @Max(value = 2100, message = "Año final debe ser menor o igual a 2100")
    private Integer annoFin;
    
    /*
     * Jefe de proyecto asignado.
     */
    private T_EmpleadoDTO jefeProyecto;
    
    /*
     * Validación personalizada: annoFin >= annoIni.
     */
    @AssertTrue(message = "El año final debe ser mayor o igual al año inicial")
    public boolean isAnnosValidos() {
        if (annoIni == null || annoFin == null) {
            return true; // Dejar que @NotNull lo maneje
        }
        return annoFin >= annoIni;
    }
}
