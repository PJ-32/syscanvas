package com.syscanvas.syscanvas.dto.SC;

import java.time.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;

/*
 * DTO para Tarea con validaciones Jakarta.
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_TareaDTO {
    /*
     * Código de la tarea.
     */
    private Long codTarea;
    
    /*
     * Nombre de la tarea.
     */
    @NotBlank(message = "El nombre de la tarea es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nomTarea;
    
    /*
     * Descripción detallada de la tarea.
     */
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String desTarea;
    
    /*
     * Fecha de creación (generada automáticamente).
     */
    private LocalDateTime fecCreacion;
    
    /*
     * Fecha de última modificación.
     */
    private LocalDateTime fecModificacion;
    
    /*
     * Número de orden de la tarea dentro de la etapa.
     */
    @Positive(message = "El número de tarea debe ser positivo")
    private Long numTarea;
    
    /*
     * Vigencia de la tarea.
     * - 1 = Pendiente
     * - 0 = Completada
     */
    @NotNull(message = "La vigencia es obligatoria")
    @Min(value = 0, message = "La vigencia debe ser 0 o 1")
    @Max(value = 1, message = "La vigencia debe ser 0 o 1")
    private Long vigente;
    
    /*
     * Etapa a la que pertenece la tarea.
     */

    @NotNull(message = "La etapa es obligatoria")
    private Long codEtapa;
    
    @NotNull(message = "El canvas es obligatorio")
    private Long codCanvas;

    /*
     * Persona asignada a la tarea (Analista).
     */
    @Valid
    private Long codPersona;
    //private T_PersonaDTO persona;
}