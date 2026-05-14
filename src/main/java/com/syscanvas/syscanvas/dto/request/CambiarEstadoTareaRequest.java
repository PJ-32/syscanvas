package com.syscanvas.syscanvas.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request para cambiar estado de una tarea.
 */

public record CambiarEstadoTareaRequest (
    @NotNull(message = "El parámetro 'completar' es obligatorio")
    Boolean completar
) {
    
}