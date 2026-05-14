package com.syscanvas.syscanvas.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request para cambiar estado editable de un canvas.
 */

public record CambiarEstadoEditableRequest (
    @NotNull(message = "El parámetro 'editable' es obligatorio")
    Boolean editable
) {
    
}